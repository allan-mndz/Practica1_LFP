import java.util.ArrayList;
import java.util.List;

%%
// Declaraciones y opciones

%public
%class LexerPromptZal

%line
%column

%type Token

LETRA = [a-zA-Z_]
DIGITO = [0-9]

ID =    {LETRA}({LETRA}|{DIGITO})*

ENTERO =    {DIGITO}+
DECIMAL =   {ENTERO}"."{ENTERO}

CADENA = \"[^\"]*\"

COMENTARIO_LINEA = "//".* 
COMENTARIO_BLOQUE = "/*"~"*/"

ESPACIO = [ \t\n\r]+

%%

// REGLAS LEXICAS
{ESPACIO} { /* Ignorar espacios en blanco */ }
{COMENTARIO_LINEA} { /* Ignorar comentarios de línea */ }
{COMENTARIO_BLOQUE} { /* Ignorar comentarios de bloque */ }

// DIRECTIVAS
"@modelo" | "@rol" | "@formato" { return new Token("DIRECTIVA", yytext(), yyline + 1, yycolumn + 1); }

// IDENTIFICADORES

    // PALABRAS RESERVADAS
    "AGENTE"    { return new Token("AGENTE",    yytext(), yyline + 1, yycolumn + 1); }
    "contexto"  { return new Token("CONTEXTO",  yytext(), yyline + 1, yycolumn + 1); }
    "variable"  { return new Token("VARIABLE",  yytext(), yyline + 1, yycolumn + 1); }
    "EJECUTAR"  { return new Token("EJECUTAR",  yytext(), yyline + 1, yycolumn + 1); }
    "EXPORTAR"  { return new Token("EXPORTAR",  yytext(), yyline + 1, yycolumn + 1); }
    "CARGAR"    { return new Token("CARGAR",    yytext(), yyline + 1, yycolumn + 1); }

    // COMANDO IA
    "PREGUNTAR" | "GENERAR" | "RESUMIR" | "ANALIZAR" | "TRADUCIR" | "CLASIFICAR" | "EXTRAER" {return new Token("COMANDO_IA", yytext(), yyline + 1, yycolumn + 1); } 

    // CONECTORES
    "SOBRE" | "DESDE" | "EN" | "COMO" { return new Token("CONECTOR", yytext(), yyline + 1, yycolumn + 1); }
    
// AHI QUEDO


// SIMBOLOS UNICOS
"->"    { return new Token("FLECHA",    yytext(), yyline + 1, yycolumn + 1); }
"="     { return new Token("IGUAL",     yytext(), yyline + 1, yycolumn + 1); }
"+"     { return new Token("MAS",       yytext(), yyline + 1, yycolumn + 1); }
"{"     { return new Token("LLAVE_A",   yytext(), yyline + 1, yycolumn + 1); }
"}"     { return new Token("LLAVE_C",   yytext(), yyline + 1, yycolumn + 1); }
"("     { return new Token("PAR_A",     yytext(), yyline + 1, yycolumn + 1); }
")"     { return new Token("PAR_C",     yytext(), yyline + 1, yycolumn + 1); }
","     { return new Token("COMA",      yytext(), yyline + 1, yycolumn + 1); }


// LAS REGLAS DEL INICIO
{ENTERO}    { return new Token("NUMERO",    yytext(), yyline + 1, yycolumn + 1); }
{DECIMAL}   { return new Token("NUMERO",    yytext(), yyline + 1, yycolumn + 1); }
{ID}        { return new Token("ID",        yytext(), yyline + 1, yycolumn + 1); }
{CADENA}    { return new Token("CADENA",    yytext(), yyline + 1, yycolumn + 1); }

// MANEJRO DE ERRORES
.       { return new Token("ERROR_LEXICO", yytext(), yyline + 1, yycolumn + 1); }
<<EOF>> { return new Token("EOF", "FIN DEL ARCHIVO", yyline + 1, yycolumn + 1); }
