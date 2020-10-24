# Compiladores
Projetos da disciplina de construção de compiladores

## Analisador Sintático
Dada a gramática apresentada, foram realizadas as seguintes modificações, com o propósito de remover recursões à esquerda, e assim, realizar a análise preditiva recursiva.


### lista_declarações_variáveis
Versão original:

>lista_declarações_variáveis →
 >>  lista_declarações_variáveis lista_de_identificadores: tipo;<br>
> lista_de_identificadores: tipo;

Note que possui uma recursão a esquerda o que faz ser reescrito como:

> lista_declarações_variáveis → 
>> lista_de_identificadores: tipo; lista_declarações_variáveis2
 
>lista_declarações_variáveis2 → 
>> id lista_de_identificadores2: tipo; lista_declarações_variáveis2 <br>
>> ϵ

### lista_identificadores
Versão original:

> lista_de_identificadores → 
>> id <br>
>> lista_de_identificadores, id

Reescrito como:

> lista_de_identificadores →
>> id lista_de_identificadores2

> lista_de_identificadores2 → 
>>, id lista_de_identificadores2 <br> 
>>ϵ

### declarações_subprogramas
Versão original:

>declarações_de_subprogramas →
>> declarações_de_subprogramas declaração_de_subprograma;
>> <br>ε

Reescrito como:

> declarações_de_subprogramas → 
>> declarações_de_subprogramas2
>> <br>ε

> declarações_de_subprogramas2 → 
>> declaração_de_subprograma ; declarações_de_subprogramas2
>> <br> ε