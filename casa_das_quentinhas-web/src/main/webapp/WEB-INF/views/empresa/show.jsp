<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page isELIgnored="false"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="sec"
	uri="http://www.springframework.org/security/tags"%>

<h3 class="page-header">
	Visualizar Empresa <span class="label label-default">${empresa.id}</span>
</h3>

<div class="row">
	<div class="col-md-4">
		<p>
			<strong>Razão social</strong>
		</p>
		<p>${empresa.razaoSocial}</p>
	</div>
	<div class="col-md-4">
		<p>
			<strong>Nome fantasia</strong>
		</p>
		<p>${empresa.nomeFantasia}</p>
	</div>
	<div class="col-md-2">
		<p>
			<strong>CNPJ</strong>
		</p>
		<p>${empresa.formattedCnpj}</p>
	</div>
	<div class="col-md-2">
		<p>
			<strong>Tipo de empresa</strong>
		</p>
		<p>${empresa.tipo.descricao}</p>
	</div>
</div>

<div class="row">	
	<div class="col-md-6">
		<p>
			<strong>E-mail</strong>
		</p>
		<p>${empresa.email}</p>
	</div>
	<div class="col-md-2">
		<p>
			<strong>Telefone</strong>
		</p>
		<p>${empresa.formattedTelefone}</p>
	</div>
	<div class="col-md-4">
		<p>
			<strong>Regime tributário</strong>
		</p>
		<p>${empresa.regimeTributario.descricao}</p>
	</div>	
</div>

<div class="row">
	<div class="col-md-3">
		<p>
			<strong>Inscrição estadual</strong>
		</p>
		<p>${empresa.inscricaoEstadual}</p>
	</div>
	<div class="col-md-3">
		<p>
			<strong>Inscrição estadual do subst. tributário</strong>
		</p>
		<p>${empresa.inscricaoEstadualST}</p>
	</div>
	<div class="col-md-3">
		<p>
			<strong>Inscrição municipal</strong>
		</p>
		<p>${empresa.inscricaoMuncipal}</p>
	</div>
	<div class="col-md-3">
		<p>
			<strong>Códido CNAE</strong>
		</p>
		<p>${empresa.codigoCnae}</p>
	</div>
</div>

<div class="row">	
	<div class="col-md-6">
		<p>
			<strong>Cidade</strong>
		</p>
		<p>${empresa.cidade.nomeWithEstadoSigla}</p>
	</div>
	<div class="col-md-2">
		<p>
			<strong>CEP</strong>
		</p>
		<p>${empresa.endereco.formattedCep}</p>
	</div>
	<div class="col-md-4">
		<p>
			<strong>Bairro</strong>
		</p>
		<p>${empresa.endereco.bairro}</p>
	</div>	
</div>

<div class="row">	
	<div class="col-md-8">
		<p>
			<strong>Logradouro</strong>
		</p>
		<p>${empresa.endereco.logradouro}</p>
	</div>
	<div class="col-md-4">
		<p>
			<strong>Número do imóvel</strong>
		</p>
		<p>${empresa.endereco.numeroImovel}</p>
	</div>	
</div>

<div class="row">	
	<div class="col-md-6">
		<p>
			<strong>Complemento</strong>
		</p>
		<p>${empresa.endereco.complemento}</p>
	</div>
	<div class="col-md-6">
		<p>
			<strong>Ponto de referência</strong>
		</p>
		<p>${empresa.endereco.pontoReferencia}</p>
	</div>	
</div>

<hr />
<div id="actions" class="row">
	<div class="col-md-12">
		<sec:authorize access="hasRole('GERENTE') or hasRole('ATENDENTE')">
			<a class="btn btn-warning"
				href="<c:url value='/empresa/${empresa.id}/edit' />">Alterar</a>
		</sec:authorize>
		<sec:authorize access="hasRole('GERENTE')">
			<button id="delete" class="btn btn-danger" value="${empresa.id}">Excluir</button>
		</sec:authorize>
		<a href="javascript:history.back()" class="btn btn-default">Cancelar</a>
	</div>
</div>

<script type="text/javascript">
	var MAIN_PAGE = '<c:url value='/${mainPage}' />';
	var MODAL_LABEL = 'Excluir Empresa';
	var MODAL_BODY = 'Deseja realmente excluir esta Empresa?';
</script>
<script src="<c:url value='/static/js/casa_das_quentinhas-show.js' />"></script>

<%@include file="../../layouts/delete_modal.jsp"%>