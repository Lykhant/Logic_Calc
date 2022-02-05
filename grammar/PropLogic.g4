grammar PropLogic;

// parser

expr 	: NOT expr											#negated
		|'(' expr ')' 										#parenthesis  
		| <assoc=right> left=expr (AND|OR) right=expr					#conjDisjunction
		| <assoc=right> left=expr (IMPLICATION|BICONDITIONAL) right=expr	#implications
   		| atom=ELEMENT											#atom
		;
   
 // lexer   
ELEMENT : [a-zA-Z];

BICONDITIONAL : '<->' ;
IMPLICATION : '->' ;
AND : 'and' ;
OR : 'or' ;
NOT : '!';


WS : [ \r\n\t] + -> skip ;