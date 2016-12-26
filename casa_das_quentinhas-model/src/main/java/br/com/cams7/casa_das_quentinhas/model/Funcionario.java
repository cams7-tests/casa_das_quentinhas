package br.com.cams7.casa_das_quentinhas.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

import br.com.cams7.app.model.AbstractEntity;

@SuppressWarnings("serial")
@Entity
@Table(name = "funcionario")
public class Funcionario extends AbstractEntity<Integer> {

	@Id
	@Column(name = "id_funcionario", nullable = false)
	private Integer id;

	@OneToOne(optional = false)
	@PrimaryKeyJoinColumn(name = "id_funcionario", referencedColumnName = "id_usuario")
	private Usuario usuario;

	@Enumerated(EnumType.ORDINAL)
	@Column(nullable = false)
	private Funcao funcao;

	@Override
	public Integer getId() {
		return id;
	}

	@Override
	public void setId(Integer id) {
		this.id = id;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public Funcao getFuncao() {
		return funcao;
	}

	public void setFuncao(Funcao funcao) {
		this.funcao = funcao;
	}

	@Override
	public int hashCode() {
		final int PRIME = 31;
		int hashCode = super.hashCode();
		hashCode = PRIME * hashCode + ((funcao == null) ? 0 : funcao.hashCode());
		return hashCode;
	}

	@Override
	public boolean equals(Object object) {
		if (!super.equals(object))
			return false;

		Funcionario funcionario = (Funcionario) object;
		if (funcao == null) {
			if (funcionario.funcao != null)
				return false;
		} else if (!funcao.equals(funcionario.funcao))
			return false;

		return true;
	}

	public enum Funcao {
		/* GERENTE */ADMIN, /* ATENDENTE */DBA, /* ENTREGADOR */USER
	}

}
