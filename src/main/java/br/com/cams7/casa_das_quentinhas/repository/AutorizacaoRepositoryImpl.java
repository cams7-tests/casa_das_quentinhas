package br.com.cams7.casa_das_quentinhas.repository;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import br.com.cams7.casa_das_quentinhas.entity.AutorizacaoEntity;

@Repository
public class AutorizacaoRepositoryImpl extends AbstractRepository<Integer, AutorizacaoEntity>
		implements AutorizacaoRepository {

	public AutorizacaoEntity findById(Integer id) {
		return getByKey(id);
	}

	public AutorizacaoEntity findByPapel(String papel) {
		Criteria crit = createEntityCriteria();
		crit.add(Restrictions.eq("papel", papel));
		return (AutorizacaoEntity) crit.uniqueResult();
	}

	@SuppressWarnings("unchecked")
	public List<AutorizacaoEntity> findAll() {
		Criteria crit = createEntityCriteria();
		crit.addOrder(Order.asc("papel"));
		return (List<AutorizacaoEntity>) crit.list();
	}

}