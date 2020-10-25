# Compiladores
Projetos da disciplina de construção de compiladores

## Analisador Sintático
Dada a gramática apresentada, foram realizadas as seguintes modificações, com o propósito de remover recursões à esquerda, e assim, realizar a análise preditiva recursiva, todas as outras regras não mencionadas foram mantidas conforme especificadas.


### lista_declarações_variáveis
Versão original:

>lista_declarações_variáveis →
>>  lista_declarações_variáveis lista_de_identificadores: tipo;<br>
>>| lista_de_identificadores: tipo;

Note que possui uma recursão a esquerda o que faz ser reescrito como:

> lista_declarações_variáveis → 
>> lista_de_identificadores: tipo; lista_declarações_variáveis2
 
>lista_declarações_variáveis2 → 
>> id lista_de_identificadores2: tipo; lista_declarações_variáveis2 <br>
>>| ϵ

### lista_identificadores
Versão original:

> lista_de_identificadores → 
>> id <br>
>>| lista_de_identificadores, id

Reescrito como:

> lista_de_identificadores →
>> id lista_de_identificadores2

> lista_de_identificadores2 → 
>>, id lista_de_identificadores2 <br> 
>>| ϵ

### declarações_subprogramas
Versão original:

>declarações_de_subprogramas →
>> declarações_de_subprogramas declaração_de_subprograma;
>> <br>| ε

Reescrito como:

> declarações_de_subprogramas → 
>> declarações_de_subprogramas2 ε

> declarações_de_subprogramas2 → 
>> declaração_de_subprograma ; declarações_de_subprogramas2
>> <br>| ε

### lista_de_parametros
Versão original:
>lista_de_parametros → 
>>lista_de_identificadores: tipo
>><br>| lista_de_parametros; lista_de_identificadores: tipo

Reescrito como:
>lista_de_parametros →
>> lista_de_identificadores: tipo lista_de_parametros2

> lista_de_parametros2 → 
>> ; id lista_de_identificadores2 : tipo
>> <br>| ε

### lista_de_comandos
Versão original:
>lista_de_comandos →
 >> comando
 >> <br>| lista_de_comandos; comando

Reescrito como:
>lista_de_comandos → 
>> comando lista_de_comandos2

>lista_de_comandos2 →
>> ; comando lista_de_comandos2
>> <br>| ε

### lista_de_expressões
Versão original:
>lista_de_expressões →
>> expressão
>><br>| lista_de_expressões , expressão

Reescrito como:
>lista_de_expressoes →
>> expressao lista_de_expressoes2

>lista_de_expressoes2 → 
>>, expressao lista_de_expressoes2 
>><br>| ε

### expressão_simples
Versão original:
>expressão_simples → 
>>termo
>><br>| sinal termo
>><br>| expressão_simples op_aditivo termo

Reescrito como:
>expressao_simples →
>> termo expressao_simples2 
>><br>| sinal termo expressao_simples2

>expressao_simples2 →
>> op_aditivo termo expressao_simples2
>>| ε

### termo
Versão original:
>termo → 
>>fator
>><br>| termo op_multiplicativo fator

Reescrito como:
>termo →
>> fator termo2

> termo2 →
>> op_multiplicativo fator termo2
>><br>| ε