<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page isELIgnored="false"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="sec"
	uri="http://www.springframework.org/security/tags"%>


<h3 class="page-header">Visualizar Usuário #${usuario.id}</h3>

	<div class="row">
		<div class="col-md-6">
      		<p><strong>Nome</strong></p>
  	  		<p>${usuario.nome}</p>
    	</div>
    	<div class="col-md-6">
      		<p><strong>Sobrenome</strong></p>
  	  		<p>${usuario.sobrenome}</p>
    	</div>
	</div>	
	
	<div class="row">
		<div class="col-md-6">
      		<p><strong>E-mail</strong></p>
  	  		<p>${usuario.email}</p>
    	</div>
    	<div class="col-md-6">
      		<p><strong>Autorização</strong></p>
  	  		<p></p>
    	</div>
	</div>

	<hr />
 	<div id="actions" class="row">
   		<div class="col-md-12">   
   			<sec:authorize access="hasRole('ADMIN') or hasRole('DBA')">
				<a class="btn btn-warning" href="<c:url value='/usuario/${usuario.id}/edit' />">Alterar</a>
			</sec:authorize>
			<sec:authorize access="hasRole('ADMIN')">
				<!--a class="btn btn-danger" href="#" data-toggle="modal" data-target="#delete-modal">Excluir</a-->
				<a class="btn btn-danger" href="<c:url value='/usuario/${usuario.id}/delete' />">Excluir</a>
			</sec:authorize>  		
			<a href="<c:url value='/usuario/list' />" class="btn btn-default">Cancelar</a>
   		</div>
 	</div>