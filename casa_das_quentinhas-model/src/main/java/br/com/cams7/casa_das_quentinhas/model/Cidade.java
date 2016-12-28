/**
 * 
 */
package br.com.cams7.casa_das_quentinhas.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import br.com.cams7.app.model.AbstractEntity;

/**
 * @author César Magalhães
 *
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "cidade")
public class Cidade extends AbstractEntity<Integer> {

	@Id
	@SequenceGenerator(name = "cidade_id_seq", sequenceName = "cidade_id_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "cidade_id_seq")
	@Column(name = "id_cidade", nullable = false)
	private Integer id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "id_uf", referencedColumnName = "id_uf")
	private Estado estado;

	@Column(length = 64, nullable = false)
	private String nome;

	@Column(name = "codigo_ibge", length = 7, unique = true, nullable = false)
	private Long codigoIbge;

	@Column(length = 2, nullable = false)
	private Byte ddd;

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.com.cams7.app.model.AbstractEntity#getId()
	 */
	@Override
	public Integer getId() {
		return id;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.com.cams7.app.model.AbstractEntity#setId(java.io.Serializable)
	 */
	@Override
	public void setId(Integer id) {
		this.id = id;
	}

	public Estado getEstado() {
		return estado;
	}

	public void setEstado(Estado estado) {
		this.estado = estado;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public Long getCodigoIbge() {
		return codigoIbge;
	}

	public void setCodigoIbge(Long codigoIbge) {
		this.codigoIbge = codigoIbge;
	}

	public Byte getDdd() {
		return ddd;
	}

	public void setDdd(Byte ddd) {
		this.ddd = ddd;
	}

	@Override
	public int hashCode() {
		final int PRIME = 31;
		int hashCode = super.hashCode();
		hashCode = PRIME * hashCode + ((codigoIbge == null) ? 0 : codigoIbge.hashCode());
		return hashCode;
	}

	@Override
	public boolean equals(Object object) {
		if (!super.equals(object))
			return false;

		Cidade cidade = (Cidade) object;
		if (codigoIbge == null) {
			if (cidade.codigoIbge != null)
				return false;
		} else if (!codigoIbge.equals(cidade.codigoIbge))
			return false;

		return true;
	}

}
