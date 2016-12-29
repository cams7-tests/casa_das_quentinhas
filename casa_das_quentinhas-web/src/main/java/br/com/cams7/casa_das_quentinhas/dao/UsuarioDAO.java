package br.com.cams7.casa_das_quentinhas.dao;

import br.com.cams7.app.dao.BaseDAO;
import br.com.cams7.casa_das_quentinhas.model.Usuario;

public interface UsuarioDAO extends BaseDAO<Usuario, Integer> {

	/**
	 * @param email
	 *            E-mail do usuário
	 * @return Usuário
	 */
	Usuario getUsuarioByEmail(String email);

	/**
	 * @param email
	 *            E-mail do usuário
	 * @return ID do usuário
	 */
	Integer getUsuarioIdByEmail(String email);

	/**
	 * @param id
	 *            ID do usuário
	 * @return Senha criptografada
	 */
	String getUsuarioSenhaById(Integer id);

	/**
	 * @param empresaId
	 *            ID da empresa
	 * @return ID do usuário
	 */
	Integer getUsuarioAcessoIdByEmpresaId(Integer empresaId);
}
