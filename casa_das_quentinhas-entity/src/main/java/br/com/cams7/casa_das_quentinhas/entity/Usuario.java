package br.com.cams7.casa_das_quentinhas.entity;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

import br.com.cams7.app.entity.AbstractEntity;

@SuppressWarnings("serial")
@Entity
@Table(name = "usuario")
public class Usuario extends AbstractEntity<Integer> {

	@Id
	@SequenceGenerator(name = "usuario_id_seq", sequenceName = "usuario_id_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "usuario_id_seq")
	@Column(name = "id_usuario", nullable = false)
	private Integer id;

	@NotEmpty
	@Size(min = 5, max = 50)
	@Email
	@Column(unique = true, nullable = false)
	private String email;

	@Column(name = "senha", length = 100, nullable = false)
	private String senhaCriptografada;

	@Enumerated(EnumType.ORDINAL)
	@Column(name = "tipo_usuario", nullable = false)
	private Tipo tipo;

	// @OneToOne(fetch = FetchType.LAZY, mappedBy = "usuario")
	// private Funcionario funcionario;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "usuarioCadastro")
	private List<Funcionario> funcionarios;

	// @OneToOne(fetch = FetchType.LAZY, mappedBy = "usuarioAcesso")
	// private Empresa empresa;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "usuarioCadastro")
	private List<Empresa> empresas;

	// @OneToOne(fetch = FetchType.LAZY, mappedBy = "usuarioAcesso")
	// private Cliente cliente;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "usuarioCadastro")
	private List<Cliente> clientes;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "usuarioCadastro")
	private List<Produto> produtos;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "usuarioCadastro")
	private List<Pedido> pedidos;

	@Transient
	private String senha;

	@Transient
	private String confirmacaoSenha;

	/**
	 * 
	 */
	public Usuario() {
		super();
	}

	/**
	 * @param id
	 */
	public Usuario(Integer id) {
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
	 * @return E-mail de acesso
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @param email
	 *            E-mail de acesso
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * @return Senha criptografada
	 */
	public String getSenhaCriptografada() {
		return senhaCriptografada;
	}

	/**
	 * @param senhaCriptografada
	 *            Senha criptografada
	 */
	public void setSenhaCriptografada(String senhaCriptografada) {
		this.senhaCriptografada = senhaCriptografada;
	}

	/**
	 * @return Tipo de usu??rio
	 */
	public Tipo getTipo() {
		return tipo;
	}

	/**
	 * @param tipo
	 *            Tipo de usu??rio
	 */
	public void setTipo(Tipo tipo) {
		this.tipo = tipo;
	}

	/**
	 * @return Funcion??rio com acesso atrav??s do usu??rio
	 */
	// public Funcionario getFuncionario() {
	// return funcionario;
	// }

	/**
	 * @param funcionario
	 *            Funcion??rio com acesso atrav??s do usu??rio
	 */
	// public void setFuncionario(Funcionario funcionario) {
	// this.funcionario = funcionario;
	// }

	/**
	 * @return Funcion??rios cadastrados pelo usu??rio
	 */
	public List<Funcionario> getFuncionarios() {
		return funcionarios;
	}

	/**
	 * @param funcionarios
	 *            Funcion??rios cadastrados pelo usu??rio
	 */
	public void setFuncionarios(List<Funcionario> funcionarios) {
		this.funcionarios = funcionarios;
	}

	/**
	 * @return Empresa com acesso atrav??s do usu??rio
	 */
	// public Empresa getEmpresa() {
	// return empresa;
	// }

	/**
	 * @param empresa
	 *            Empresa com acesso atrav??s do usu??rio
	 */
	// public void setEmpresa(Empresa empresa) {
	// this.empresa = empresa;
	// }

	/**
	 * @return Empresas cadastradas pelo usu??rio
	 */
	public List<Empresa> getEmpresas() {
		return empresas;
	}

	/**
	 * @param empresas
	 *            Empresas cadastradas pelo usu??rio
	 */
	public void setEmpresas(List<Empresa> empresas) {
		this.empresas = empresas;
	}

	/**
	 * @return Cliente com acesso atrav??s do usu??rio
	 */
	// public Cliente getCliente() {
	// return cliente;
	// }

	/**
	 * @param cliente
	 *            Cliente com acesso atrav??s do usu??rio
	 */
	// public void setCliente(Cliente cliente) {
	// this.cliente = cliente;
	// }

	/**
	 * @return Clientes cadastrados pelo usu??rio
	 */
	public List<Cliente> getClientes() {
		return clientes;
	}

	/**
	 * @param clientes
	 *            Clientes cadastrados pelo usu??rio
	 */
	public void setClientes(List<Cliente> clientes) {
		this.clientes = clientes;
	}

	/**
	 * @return Produtos cadastrados pelo usu??rio
	 */
	public List<Produto> getProdutos() {
		return produtos;
	}

	/**
	 * @param produtos
	 *            Produtos cadastrados pelo usu??rio
	 */
	public void setProdutos(List<Produto> produtos) {
		this.produtos = produtos;
	}

	/**
	 * @return Pedidos cadastrados pelo usu??rio
	 */
	public List<Pedido> getPedidos() {
		return pedidos;
	}

	/**
	 * @param pedidos
	 *            Pedidos cadastrados pelo usu??rio
	 */
	public void setPedidos(List<Pedido> pedidos) {
		this.pedidos = pedidos;
	}

	/**
	 * @return Senha sem criptografia
	 */
	public String getSenha() {
		return senha;
	}

	/**
	 * @param senha
	 *            Senha sem criptografia
	 */
	public void setSenha(String senha) {
		this.senha = senha;
	}

	/**
	 * @return Senha de confirma????o
	 */
	public String getConfirmacaoSenha() {
		return confirmacaoSenha;
	}

	/**
	 * @param confirmacaoSenha
	 *            Senha de confirma????o
	 */
	public void setConfirmacaoSenha(String confirmacaoSenha) {
		this.confirmacaoSenha = confirmacaoSenha;
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
		hashCode = PRIME * hashCode + ((email == null) ? 0 : email.hashCode());
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

		Usuario usuario = (Usuario) object;
		if (email == null) {
			if (usuario.email != null)
				return false;
		} else if (!email.equals(usuario.email))
			return false;

		return true;
	}

	/**
	 * @author C??sar Magalh??es
	 * 
	 *         Tipo de Usu??rio
	 */
	public enum Tipo {
		FUNCIONARIO("Funcion??rio(a)"), EMPRESA("Empresa"), CLIENTE("Cliente");
		private String descricao;

		private Tipo(String descricao) {
			this.descricao = descricao;
		}

		public Tipo getTipo() {
			return values()[ordinal()];
		}

		public String getDescricao() {
			return descricao;
		}
	}

	/**
	 * @author C??sar Magalh??es
	 *
	 *         Rel????o entre o usu??rio e as outras entidades
	 */
	public enum RelacionamentoUsuario {
		ACESSO, CADASTRO
	}

}
