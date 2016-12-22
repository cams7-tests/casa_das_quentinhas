<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page isELIgnored="false"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<h3 class="page-header">Editar Usuário</h3>

<form:form method="POST" modelAttribute="usuario">
	<form:input type="hidden" path="id" id="id" />

	<%@include file="form.jsp"%>

	<hr />
	<div id="actions" class="row">
		<div class="col-md-12">			
			<input type="submit" value="Alterar" class="btn btn-primary" />
			<a href="<c:url value='/usuario/list' />" class="btn btn-default">Cancelar</a>
		</div>
	</div>
</form:form>