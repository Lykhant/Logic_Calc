grammar Logica;

// parser

expr 	: NOT expr											#negado
		|'(' expr ')' 										#parentesis  
		| <assoc=right> left=expr (AND|OR) right=expr					#conjDisyuncion
		| <assoc=right> left=expr (IMPLICA|BICONDICIONAL) right=expr	#implicaciones
   		| atom=ELEMENTO											#fAtomica
		;
   
 // lexer   
ELEMENTO : [a-zA-Z];

BICONDICIONAL : '<->' ;
IMPLICA : '->' ;
AND : 'and' ;
OR : 'or' ;
NOT : '!';


WS : [ \r\n\t] + -> skip ;