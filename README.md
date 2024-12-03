
# Backend events memories

Um projeto pessoal focado em criar um sistema que cria e convida pessoas para eventos, onde estas podem compartilhas suas fotos tiradas durante a realização do evento.

## Funcionalidades

- Registro de usuario
- login de usuario
- Criação de evento
- Convite de usuario para evento
- Upload de imagens do evento
- Redefinição de senha de usuario


## Documentação da API

## login

### Efetua login de usuario

```http
  POST /auth/login
```

#### Parametros do body

| Parâmetro   | Tipo       | Descrição                           |
| :---------- | :--------- | :---------------------------------- |
| `email` | `string` | **Obrigatório**. Email do usuario|
| `password` | `string` | **Obrigatório**. Senha do usuario|

#### Exemplo de resposta
```json
{
    "name": "giovana",
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9."
}
```

## Register

### Efetua registro de usuario

```http
  POST /auth/register
```
#### Parametros do body

| Parâmetro   | Tipo       | Descrição                           |
| :---------- | :--------- | :---------------------------------- |
| `email` | `string` | **Obrigatório**. Email para registro de usuario|
| `password` | `string` | **Obrigatório**. Senha do usuario|
| `name` | `string` | **Obrigatório**. Nome do usuario|

#### Exemplo de resposta
```json
{
    "name": "giovana",
    "email": "exemplo@gmail.com"
}
```

## Redefinir senha

### Envia link com o token para redefinir senha do usuario

```http
  POST /auth/user/forgotPassword/{email}
```
#### Parametros da url

| Parâmetro   | Tipo       | Descrição                           |
| :---------- | :--------- | :---------------------------------- |
| `email` | `string` | **Obrigatório**. Email para envio do link com token|

### Enviar nova senha de usuario

```http
  POST /auth/user/resetPassword
```
#### Parametros do body

| Parâmetro   | Tipo       | Descrição                           |
| :---------- | :--------- | :---------------------------------- |
| `token` | `string` | **Obrigatório**. Token recebido pelo link do email|
| `password` | `string` | **Obrigatório**. Senha do usuario|

## Evento

### Criar novo evento

```http
  POST /event/create
```
#### Parametros do Header
| Header   | Tipo       | Descrição                           |
| :---------- | :--------- | :---------------------------------- |
| `Authorization` | `string` | **Obrigatório**. Obrigatório. Token de autenticação no formato Bearer <token>.|

#### Parametros do body
| Parâmetro   | Tipo       | Descrição                           |
| :---------- | :--------- | :---------------------------------- |
| `name` | `string` | **Obrigatório**. Nome do evento|
| `description` | `string` | **Opcional**. Descriçao do evento|
| `date` | `string` | **Opcional**. Data do evento <dd/mm/aaaa>|
| `location` | `string` | **Opcional**. Endereço do evento|

### Deletar evento

```http
  DELETE /event/delete/{evend_id}
```

#### Parametros do Header
| Header   | Tipo       | Descrição                           |
| :---------- | :--------- | :---------------------------------- |
| `Authorization` | `string` | **Obrigatório**. Obrigatório. Token de autenticação no formato Bearer <token>.|

#### Parametros da url

| Parâmetro   | Tipo       | Descrição                           |
| :---------- | :--------- | :---------------------------------- |
| `evend_id` | `string` | **Obrigatório**. Id do evento a ser deletado|

### Listar eventos do usuario

```http
  GET /event/list
```
#### Parametros do Header
| Header   | Tipo       | Descrição                           |
| :---------- | :--------- | :---------------------------------- |
| `Authorization` | `string` | **Obrigatório**. Obrigatório. Token de autenticação no formato Bearer <token>.|


### Enviar imagens do evento

```http
  POST /images/upload/eventimages/{event_id}
```

#### Parametros do Header
| Header   | Tipo       | Descrição                           |
| :---------- | :--------- | :---------------------------------- |
| `Authorization` | `string` | **Obrigatório**. Obrigatório. Token de autenticação no formato Bearer <token>.|

#### Parametros da url

| Parâmetro   | Tipo       | Descrição                           |
| :---------- | :--------- | :---------------------------------- |
| `evend_id` | `string` | **Obrigatório**. Id do evento que recebera as imagens|

#### Parâmetros no body (form-data)

| Campo  | Tipo       | Descrição                           |
| :---------- | :--------- | :---------------------------------- |
| `files` | `file` | **Obrigatório**. Arquivos a serem enviados|


### Listar imagens do evento

```http
  GET /images/list/eventimages/{event_id}
```
#### Parametros do Header
| Header   | Tipo       | Descrição                           |
| :---------- | :--------- | :---------------------------------- |
| `Authorization` | `string` | **Obrigatório**. Obrigatório. Token de autenticação no formato Bearer <token>.|

#### Parametros da url

| Parâmetro   | Tipo       | Descrição                           |
| :---------- | :--------- | :---------------------------------- |
| `evend_id` | `string` | **Obrigatório**. Id do evento que listara as imagens|

### Entrar em um evento

```http
  POST /event/invite/{event_id}
```
#### Parametros do Header
| Header   | Tipo       | Descrição                           |
| :---------- | :--------- | :---------------------------------- |
| `Authorization` | `string` | **Obrigatório**. Obrigatório. Token de autenticação no formato Bearer <token>.|

#### Parametros da url

| Parâmetro   | Tipo       | Descrição                           |
| :---------- | :--------- | :---------------------------------- |
| `evend_id` | `string` | **Obrigatório**. Id do evento que deseja fazer parte|

## Profile

### Listar informaçoes do profile do usuario

```http
  GET /profile
```

#### Parametros do Header
| Header   | Tipo       | Descrição                           |
| :---------- | :--------- | :---------------------------------- |
| `Authorization` | `string` | **Obrigatório**. Obrigatório. Token de autenticação no formato Bearer <token>.|

## Instalação

1- Clone o repositorio

2- Atualize as variaveis de ambiente em src/main/java/resources/aplication.properties

3- Execute o arquivo LoginAuthApiApplication

## Stack utilizada

**Back-end:** java 17, postgresql, Spring boot

