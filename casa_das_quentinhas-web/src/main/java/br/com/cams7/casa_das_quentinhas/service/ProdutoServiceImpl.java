/**
 * 
 */
package br.com.cams7.casa_das_quentinhas.service;

import java.util.Date;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.cams7.app.service.AbstractService;
import br.com.cams7.app.utils.AppNotFoundException;
import br.com.cams7.casa_das_quentinhas.dao.ProdutoDAO;
import br.com.cams7.casa_das_quentinhas.model.Manutencao;
import br.com.cams7.casa_das_quentinhas.model.Produto;
import br.com.cams7.casa_das_quentinhas.model.Usuario;

/**
 * @author César Magalhães
 *
 */
@Service
@Transactional
public class ProdutoServiceImpl extends AbstractService<ProdutoDAO, Produto, Integer> implements ProdutoService {

	@Autowired
	private UsuarioService usuarioService;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * br.com.cams7.app.service.AbstractService#persist(br.com.cams7.app.model.
	 * AbstractEntity, java.lang.String)
	 */
	@Override
	public void persist(Produto produto) {
		Integer usuarioId = usuarioService.getUsuarioIdByEmail(getUsername());
		produto.setUsuarioCadastro(new Usuario(usuarioId));

		Manutencao manutencao = new Manutencao();
		manutencao.setCadastro(new Date());
		manutencao.setAlteracao(new Date());

		produto.setManutencao(manutencao);

		super.persist(produto);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * br.com.cams7.app.service.AbstractService#update(br.com.cams7.app.model.
	 * AbstractEntity)
	 */
	@Override
	public void update(Produto produto) {
		verificaIdAndCadastro(produto.getId(),
				produto.getManutencao() != null ? produto.getManutencao().getCadastro() : null);

		Integer usuarioId = getUsuarioCadastroIdByProdutoId(produto.getId());
		produto.setUsuarioCadastro(new Usuario(usuarioId));

		produto.getManutencao().setAlteracao(new Date());

		super.update(produto);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * br.com.cams7.casa_das_quentinhas.dao.ProdutoDAO#getProdutoById(java.lang.
	 * Integer)
	 */
	@Transactional(readOnly = true, noRollbackFor = AppNotFoundException.class)
	@Override
	public Produto getProdutoById(Integer id) {
		return getDao().getProdutoById(id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.com.cams7.casa_das_quentinhas.dao.ProdutoDAO#
	 * getUsuarioCadastroIdByProdutoId(java.lang.Integer)
	 */
	@Transactional(readOnly = true, noRollbackFor = AppNotFoundException.class)
	@Override
	public Integer getUsuarioCadastroIdByProdutoId(Integer produtoId) {
		return getDao().getUsuarioCadastroIdByProdutoId(produtoId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * br.com.cams7.casa_das_quentinhas.dao.ProdutoDAO#getProdutosByNomeOrId(
	 * java.lang.String)
	 */
	@Transactional(readOnly = true)
	@Override
	public Map<Integer, String> getProdutosByNomeOrCusto(String nomeOrCusto) {
		return getDao().getProdutosByNomeOrCusto(nomeOrCusto);
	}

}
