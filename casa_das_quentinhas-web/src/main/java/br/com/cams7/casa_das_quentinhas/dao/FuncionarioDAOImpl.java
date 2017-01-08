package br.com.cams7.casa_das_quentinhas.dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import br.com.cams7.app.dao.AbstractDAO;
import br.com.cams7.app.utils.AppNotFoundException;
import br.com.cams7.casa_das_quentinhas.model.Empresa;
import br.com.cams7.casa_das_quentinhas.model.Funcionario;
import br.com.cams7.casa_das_quentinhas.model.Funcionario.Funcao;
import br.com.cams7.casa_das_quentinhas.model.Funcionario_;
import br.com.cams7.casa_das_quentinhas.model.Usuario;

@Repository
public class FuncionarioDAOImpl extends AbstractDAO<Funcionario, Integer> implements FuncionarioDAO {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * br.com.cams7.app.dao.AbstractDAO#getFetchJoins(javax.persistence.criteria
	 * .Root)
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected List<From<?, ?>> getFetchJoins(Root<Funcionario> from) {
		List<From<?, ?>> fetchJoins = new ArrayList<>();

		Join<Funcionario, Usuario> joinUsuario = (Join<Funcionario, Usuario>) from.fetch(Funcionario_.usuario,
				JoinType.INNER);
		fetchJoins.add(joinUsuario);

		boolean noneMatch = getIgnoredJoins() == null
				|| getIgnoredJoins().stream().noneMatch(type -> type.equals(Empresa.class));
		if (noneMatch) {
			Join<Funcionario, Empresa> joinEmpresa = (Join<Funcionario, Empresa>) from.fetch(Funcionario_.empresa,
					JoinType.INNER);
			fetchJoins.add(joinEmpresa);
		}

		return fetchJoins;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * br.com.cams7.app.dao.AbstractDAO#getJoins(javax.persistence.criteria.
	 * Root)
	 */
	@Override
	protected List<From<?, ?>> getJoins(Root<Funcionario> from) {
		List<From<?, ?>> fetchJoins = new ArrayList<>();

		Join<Funcionario, Usuario> joinUsuario = (Join<Funcionario, Usuario>) from.join(Funcionario_.usuario,
				JoinType.INNER);
		fetchJoins.add(joinUsuario);

		boolean noneMatch = getIgnoredJoins() == null
				|| getIgnoredJoins().stream().noneMatch(type -> type.equals(Empresa.class));
		if (noneMatch) {
			Join<Funcionario, Empresa> joinEmpresa = (Join<Funcionario, Empresa>) from.join(Funcionario_.empresa,
					JoinType.INNER);
			fetchJoins.add(joinEmpresa);
		}

		return fetchJoins;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.com.cams7.casa_das_quentinhas.dao.FuncionarioDAO#
	 * getFuncionarioByIdAndFuncoes(java.lang.Integer,
	 * br.com.cams7.casa_das_quentinhas.model.Funcionario.Funcao[])
	 */
	@Override
	public Funcionario getFuncionarioByIdAndFuncoes(Integer id, Funcao... funcoes) {
		CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<Funcionario> cq = cb.createQuery(ENTITY_TYPE);

		Root<Funcionario> from = cq.from(ENTITY_TYPE);

		from.fetch(Funcionario_.usuario, JoinType.INNER);
		from.fetch(Funcionario_.usuarioCadastro, JoinType.INNER);
		from.fetch(Funcionario_.empresa, JoinType.INNER);

		cq.select(from);

		cq.where(cb.equal(from.get(Funcionario_.id), id), cb.or(Arrays.asList(funcoes).stream()
				.map(funcao -> cb.equal(from.get(Funcionario_.funcao), funcao)).toArray(Predicate[]::new)));

		TypedQuery<Funcionario> tq = getEntityManager().createQuery(cq);

		try {
			Funcionario funcionario = tq.getSingleResult();

			return funcionario;
		} catch (NoResultException e) {
			throw new AppNotFoundException(String.format("O funcionário (id:%s, função: [%s]) não foi encontrado...",
					id, Arrays.asList(funcoes).stream().map(funcao -> funcao.getDescricao())
							.collect(Collectors.joining(", "))));
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * br.com.cams7.casa_das_quentinhas.dao.FuncionarioDAO#getFuncionarioByCpf(
	 * java.lang.String)
	 */
	@Override
	public Integer getFuncionarioIdByCpf(String cpf) {
		LOGGER.info("CPF : {}", cpf);

		CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<Integer> cq = cb.createQuery(Integer.class);

		Root<Funcionario> from = cq.from(ENTITY_TYPE);

		cq.select(from.get(Funcionario_.id));

		cq.where(cb.equal(from.get(Funcionario_.cpf), cpf));

		TypedQuery<Integer> tq = getEntityManager().createQuery(cq);

		try {
			Integer funcionarioId = tq.getSingleResult();
			return funcionarioId;
		} catch (NoResultException e) {
			LOGGER.warn("O id do funcionário (cpf: {}) não foi encontrado...", cpf);
		}

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.com.cams7.casa_das_quentinhas.dao.FuncionarioDAO#
	 * getFuncionarioFuncaoById(java.lang.Integer)
	 */
	@Override
	public Funcao getFuncionarioFuncaoById(Integer id) {
		CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<Funcao> cq = cb.createQuery(Funcao.class);

		Root<Funcionario> from = cq.from(ENTITY_TYPE);
		cq.where(cb.equal(from.get(Funcionario_.id), id));
		cq.select(from.get(Funcionario_.funcao));

		TypedQuery<Funcao> tq = getEntityManager().createQuery(cq);

		try {
			Funcao funcao = tq.getSingleResult();
			return funcao;
		} catch (NoResultException e) {
			LOGGER.warn("A função do funcionário (id: {}) não foi encontrada...", id);
		}

		return null;
	}

}
