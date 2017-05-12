window.onload = function() {
	  document.getElementById("username").focus();
};

$(function(){
	$('#create').button();
	$('#delete').button();
	$('#back').button();
});

$(document).on('click', '#create', function(event){
	if($('#passwd').val()!=$('#repasswd').val()){
		alert("Re-enter password. The passwords do not match!")
		event.preventDefault();
	}
});

$(document).on('click', '#delete', function(event){
	if(confirm('Do you want to delete user' + $('#user').val() + '?')){
		$('#deleteUser').submit();
		alert('Deletion succesful.')
	}
	else{
	    event.preventDefault();
	}
});