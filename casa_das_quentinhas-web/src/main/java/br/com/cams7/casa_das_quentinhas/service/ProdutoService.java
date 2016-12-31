/**
 * 
 */
package br.com.cams7.casa_das_quentinhas.service;

import br.com.cams7.app.service.BaseService;
import br.com.cams7.casa_das_quentinhas.dao.ProdutoDAO;
import br.com.cams7.casa_das_quentinhas.model.Produto;

/**
 * @author César Magalhães
 *
 */
public interface ProdutoService extends BaseService<Produto, Integer>, ProdutoDAO {

}
