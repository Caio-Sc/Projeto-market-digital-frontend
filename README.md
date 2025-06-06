# Projeto Market Digital Frontend (Android)

Este repositório contém o frontend do aplicativo mobile do sistema Market Digital, inspirado no modelo do iFood. O app foi desenvolvido em Java utilizando o Android Studio e se conecta ao [backend do projeto](https://github.com/Caio-Sc/Projeto-market-digital-Backend) para fornecer uma experiência completa de marketplace digital, permitindo que usuários e vendedores acessem rapidamente as principais funcionalidades da plataforma.

## Funcionalidades

- Cadastro de usuário: Crie seu perfil no app de forma simples e intuitiva.
- Login: Acesse sua conta com segurança.
- Cadastro de vendedor: Torne-se vendedor e anuncie seus próprios produtos.
- Visualização de produtos: Explore os produtos disponíveis para compra.
- Criação e remoção de produtos (para vendedores): Gerencie facilmente seus anúncios.
- Carrinho de compras: Adicione e remova produtos do seu carrinho antes de finalizar a compra.
- Integração total com o backend para persistência e autenticação de dados.

## Tecnologias Utilizadas

- Java (Android SDK)
- Android Studio
- Consumo de APIs REST com Retrofit/Volley/HttpURLConnection
- Material Design / Layouts responsivos
- Integração com o backend Node.js/Express (Market Digital Backend)

## Instalação

1. Clone o repositório:  
   git clone https://github.com/Caio-Sc/Projeto-market-digital-Frontend.git

2. Abra o projeto no Android Studio:
   - Vá em "Open an existing project" e selecione a pasta do projeto baixado.

3. Configure as permissões de Internet no arquivo AndroidManifest.xml, caso necessário.

4. (Opcional) Ajuste a URL base das requisições de API para apontar para seu backend.

5. Compile e execute o app em seu dispositivo ou emulador Android.

## Estrutura de Pastas

- /app/src/main/java/… — Código fonte da aplicação (Activities, Fragments, Adapters, Services, etc)
- /app/src/main/res/ — Recursos como layouts, drawables, strings e temas
- /app/src/main/AndroidManifest.xml — Permissões e configurações do app

## Telas do Aplicativo

- Cadastro e login de usuários
- Cadastro de vendedor
- Listagem de produtos
- Tela de criação e remoção de produtos (somente para vendedores)
- Visualização e gerenciamento do carrinho de compras

## Comunicação com o Backend

O aplicativo faz requisições HTTP aos seguintes endpoints REST do backend:

- POST /register – Cadastro de usuário
- POST /login – Login de usuário
- POST /seller/register – Cadastro de vendedor
- POST /product – Adição de produto
- DELETE /product/:id – Remoção de produto
- POST /cart – Adição ao carrinho
- DELETE /cart/:productId – Remover do carrinho

> Certifique-se que o backend está rodando e acessível pelo endereço configurado no app.

## Contribuição

1. Faça um fork do projeto
2. Crie uma branch para sua feature: git checkout -b feature/nome-da-feature
3. Faça commit das suas alterações: git commit -m 'feat: adiciona nova feature'
4. Faça push para sua branch: git push origin feature/nome-da-feature
5. Abra um Pull Request

## Licença

Este projeto está licenciado sob os termos do arquivo LICENSE.

Desenvolvido por [Caio-Sc](https://github.com/Caio-Sc). Para dúvidas ou sugestões, abra uma issue no repositório.

