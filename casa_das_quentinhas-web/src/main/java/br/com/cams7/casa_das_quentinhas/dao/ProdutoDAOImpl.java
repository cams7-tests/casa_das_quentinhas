/**
 * 
 */
package br.com.cams7.casa_das_quentinhas.dao;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import br.com.cams7.app.dao.AbstractDAO;
import br.com.cams7.app.utils.AppNotFoundException;
import br.com.cams7.casa_das_quentinhas.model.Manutencao_;
import br.com.cams7.casa_das_quentinhas.model.Produto;
import br.com.cams7.casa_das_quentinhas.model.Produto.Tamanho;
import br.com.cams7.casa_das_quentinhas.model.Produto_;
import br.com.cams7.casa_das_quentinhas.model.Usuario_;

/**
 * @author César Magalhães
 *
 */
@Repository
public class ProdutoDAOImpl extends AbstractDAO<Produto, Integer> implements ProdutoDAO {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * br.com.cams7.app.dao.AbstractDAO#getFetchJoins(javax.persistence.criteria
	 * .Root)
	 */
	@Override
	protected List<From<?, ?>> getFetchJoins(Root<Produto> from) {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * br.com.cams7.app.dao.AbstractDAO#getJoins(javax.persistence.criteria.
	 * Root)
	 */
	@Override
	protected List<From<?, ?>> getJoins(Root<Produto> from) {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * br.com.cams7.casa_das_quentinhas.dao.ProdutoDAO#getProdutoById(java.lang.
	 * Integer)
	 */
	@Override
	public Produto getProdutoById(Integer id) {
		CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<Produto> cq = cb.createQuery(ENTITY_TYPE);

		Root<Produto> from = cq.from(ENTITY_TYPE);

		cq.select(from);

		cq.where(cb.equal(from.get(Produto_.id), id));

		TypedQuery<Produto> tq = getEntityManager().createQuery(cq);

		try {
			Produto produto = tq.getSingleResult();
			return produto;
		} catch (NoResultException e) {
			throw new AppNotFoundException(String.format("O produto (id:%s) não foi encontrado...", id));
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.com.cams7.casa_das_quentinhas.dao.ProdutoDAO#
	 * getUsuarioCadastroIdByProdutoId(java.lang.Integer)
	 */
	@Override
	public Integer getUsuarioIdByProdutoId(Integer produtoId) {
		CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<Integer> cq = cb.createQuery(Integer.class);

		Root<Produto> from = cq.from(ENTITY_TYPE);

		cq.select(from.join(Produto_.usuarioCadastro, JoinType.INNER).get(Usuario_.id));

		cq.where(cb.equal(from.get(Produto_.id), produtoId));

		TypedQuery<Integer> tq = getEntityManager().createQuery(cq);

		try {
			Integer usuarioId = tq.getSingleResult();
			return usuarioId;
		} catch (NoResultException e) {
			throw new AppNotFoundException(
					String.format("Do produto (id: %s), o id do usuário não foi encontrado...", produtoId));
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.com.cams7.casa_das_quentinhas.dao.ProdutoDAO#
	 * getUsuarioIdAndProdutoCadastroByProdutoId(java.lang.Integer)
	 */
	@Override
	public Object[] getUsuarioIdAndProdutoCadastroByProdutoId(Integer produtoId) {
		CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);

		Root<Produto> from = cq.from(ENTITY_TYPE);
		cq.select(cb.array(from.join(Produto_.usuarioCadastro, JoinType.INNER).get(Usuario_.id),
				from.get(Produto_.manutencao).get(Manutencao_.cadastro)));
		cq.where(cb.equal(from.get(Produto_.id), produtoId));

		TypedQuery<Object[]> tq = getEntityManager().createQuery(cq);

		try {
			Object[] produto = tq.getSingleResult();
			return produto;
		} catch (NoResultException e) {
			throw new AppNotFoundException(String.format(
					"Do produto (id: %s), o id do usuário e a data de cadastro do produto não foram encontrados...",
					produtoId));
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * br.com.cams7.casa_das_quentinhas.dao.ProdutoDAO#getProdutosByNomeOrId(
	 * java.lang.String)
	 */
	@Override
	public Map<Integer, String> getProdutosByNomeOrCusto(String nomeOrCusto) {
		CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);

		Root<Produto> from = cq.from(ENTITY_TYPE);

		cq.select(cb.array(from.get(Produto_.id), from.get(Produto_.nome), from.get(Produto_.tamanho)));

		nomeOrCusto = "%" + nomeOrCusto.toLowerCase() + "%";

		cq.where(cb.or(cb.like(cb.lower(from.get(Produto_.nome)), nomeOrCusto),
				cb.like(from.get(Produto_.custo).as(String.class), nomeOrCusto)));

		cq.orderBy(cb.asc(from.get(Produto_.nome)));

		TypedQuery<Object[]> tq = getEntityManager().createQuery(cq);
		tq.setMaxResults(5);

		Map<Integer, String> produtos = tq.getResultList().stream()
				.collect(Collectors.toMap(produto -> (Integer) produto[0],
						produto -> Produto.getNomeWithTamanho((String) produto[1], (Tamanho) produto[2])));

		return produtos;
	}

}
