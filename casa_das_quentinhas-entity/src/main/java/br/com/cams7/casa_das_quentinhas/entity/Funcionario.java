package br.com.cams7.casa_das_quentinhas.entity;

import static br.com.cams7.app.validator.CelularValidator.formatCelular;
import static br.com.cams7.app.validator.CelularValidator.unformatCelular;
import static br.com.cams7.app.validator.CpfValidator.formatCpf;
import static br.com.cams7.app.validator.CpfValidator.unformatCpf;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;

import br.com.cams7.app.entity.AbstractEntity;
import br.com.cams7.app.validator.CPF;
import br.com.cams7.app.validator.Celular;
import br.com.cams7.app.validator.RG;

@SuppressWarnings("serial")
@Entity
@Table(name = "funcionario")
public class Funcionario extends AbstractEntity<Integer> {

	@Id
	@Column(name = "id_funcionario", nullable = false)
	private Integer id;

	@Valid
	@OneToOne(fetch = FetchType.LAZY, optional = false)
	@PrimaryKeyJoinColumn(name = "id_funcionario", referencedColumnName = "id_usuario")
	private Usuario usuario;

	@NotNull
	@Enumerated(EnumType.ORDINAL)
	@Column
	private Funcao funcao;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_usuario_cadastro", referencedColumnName = "id_usuario")
	private Usuario usuarioCadastro;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "id_empresa", referencedColumnName = "id_empresa")
	private Empresa empresa;

	@NotEmpty
	@Size(min = 3, max = 60)
	@Column(nullable = false)
	private String nome;

	@NotEmpty
	@CPF
	@Column(nullable = false, length = 11)
	private String cpf;

	@NotEmpty
	@RG
	@Column(nullable = false, length = 10)
	private String rg;

	@NotEmpty
	@Celular
	@Column(nullable = false, length = 11)
	private String celular;

	@Embedded
	private Manutencao manutencao;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "entregador")
	private List<Pedido> pedidos;

	/**
	 * 
	 */
	public Funcionario() {
		super();
	}

	/**
	 * @param id
	 */
	public Funcionario(Integer id) {
		super(id);
	}

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

	/**
	 * @return Usu??rio de acesso ao sistema
	 */
	public Usuario getUsuario() {
		return usuario;
	}

	/**
	 * @param usuario
	 *            Usu??rio de acesso ao sistema
	 */
	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	/**
	 * @return Fun????o do funcion??rio
	 */
	public Funcao getFuncao() {
		return funcao;
	}

	/**
	 * @param funcao
	 *            Fun????o do funcion??rio
	 */
	public void setFuncao(Funcao funcao) {
		this.funcao = funcao;
	}

	/**
	 * @return Usu??rio que cadastrou o funcion??rio
	 */
	public Usuario getUsuarioCadastro() {
		return usuarioCadastro;
	}

	/**
	 * @param usuarioCadastro
	 *            Usu??rio que cadastrou o funcion??rio
	 */
	public void setUsuarioCadastro(Usuario usuarioCadastro) {
		this.usuarioCadastro = usuarioCadastro;
	}

	/**
	 * @return Empresa a qual pertence o funcion??rio
	 */
	public Empresa getEmpresa() {
		return empresa;
	}

	/**
	 * @param empresa
	 *            Empresa a qual pertence o funcion??rio
	 */
	public void setEmpresa(Empresa empresa) {
		this.empresa = empresa;
	}

	/**
	 * @return Nome do funcion??rio
	 */
	public String getNome() {
		return nome;
	}

	/**
	 * @param nome
	 *            Nome do funcion??rio
	 */
	public void setNome(String nome) {
		this.nome = nome;
	}

	/**
	 * @return CPF do funcion??rio sem formata????o
	 */
	public String getCpf() {
		return cpf;
	}

	/**
	 * @param cpf
	 *            CPF do funcion??rio sem formata????o
	 */
	public void setCpf(String cpf) {
		this.cpf = cpf;
	}

	/**
	 * @return N??mero de identidade do funcion??rio sem formata????o
	 */
	public String getRg() {
		return rg;
	}

	/**
	 * @param rg
	 *            N??mero de identidade do funcion??rio sem formata????o
	 */
	public void setRg(String rg) {
		this.rg = rg;
	}

	/**
	 * @return N??mero de celular do funcion??rio sem formata????o
	 */
	public String getCelular() {
		return celular;
	}

	/**
	 * @param celular
	 *            N??mero de celular do funcion??rio sem formata????o
	 */
	public void setCelular(String celular) {
		this.celular = celular;
	}

	/**
	 * @return Data de cadastro e altera????o
	 */
	public Manutencao getManutencao() {
		return manutencao;
	}

	/**
	 * @param manutencao
	 *            Data de cadastro e altera????o
	 */
	public void setManutencao(Manutencao manutencao) {
		this.manutencao = manutencao;
	}

	/**
	 * @return Pedidos realizados pelo entregador
	 */
	public List<Pedido> getPedidos() {
		return pedidos;
	}

	/**
	 * @param pedidos
	 *            Pedidos realizados pelo entregador
	 */
	public void setPedidos(List<Pedido> pedidos) {
		this.pedidos = pedidos;
	}

	/**
	 * @return CPF formatado
	 */
	public String getUnformattedCpf() {
		if (cpf == null || cpf.isEmpty())
			return null;

		return unformatCpf(cpf);
	}

	/**
	 * @return CPF sem formata????o
	 */
	public String getFormattedCpf() {
		if (cpf == null || cpf.isEmpty())
			return null;

		return formatCpf(cpf);
	}

	/**
	 * @return Celular formatado
	 */
	public String getUnformattedCelular() {
		if (celular == null || celular.isEmpty())
			return null;

		return unformatCelular(celular);
	}

	/**
	 * @return Celular sem formata????o
	 */
	public String getFormattedCelular() {
		if (celular == null || celular.isEmpty())
			return null;

		return formatCelular(celular);
	}

	/**
	 * @return Nome com o CPF formatado
	 */
	public String getNomeWithCpf() {
		return getNomeWithCpf(nome, cpf);
	}

	/**
	 * @param nome
	 *            Nome do funcion??rio
	 * @param cpf
	 *            CPF do funcion??rio
	 * @return Nome com o CPF formatado
	 */
	public static String getNomeWithCpf(String nome, String cpf) {
		if (nome == null || cpf == null)
			return null;

		return nome + " < " + formatCpf(cpf) + " >";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.com.cams7.app.model.AbstractEntity#hashCode()
	 */
	@Override
	public int hashCode() {
		final int PRIME = 31;
		int hashCode = super.hashCode();
		hashCode = PRIME * hashCode + ((cpf == null) ? 0 : cpf.hashCode());
		return hashCode;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see br.com.cams7.app.model.AbstractEntity#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object object) {
		if (!super.equals(object))
			return false;

		Funcionario funcionario = (Funcionario) object;
		if (cpf == null) {
			if (funcionario.cpf != null)
				return false;
		} else if (!cpf.equals(funcionario.cpf))
			return false;

		return true;
	}

	/**
	 * @author C??sar Magalh??es
	 * 
	 *         Fun????o do Funcion??rio
	 */
	public enum Funcao {
		GERENTE("Gerente"), ATENDENTE("Atendente"), ENTREGADOR("Entregador(a)");
		private String descricao;

		private Funcao(String descricao) {
			this.descricao = descricao;
		}

		public Funcao getFuncao() {
			return values()[ordinal()];
		}

		public String getDescricao() {
			return descricao;
		}
	}

}
