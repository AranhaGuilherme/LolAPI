# LolAPI Control

Front-end estatico para controlar a API Spring Boot da LolAPI.

## Rodar localmente

1. Inicie a API Spring Boot em `http://localhost:8080`.
2. Abra um terminal na pasta `frontend`.
3. Rode:

```bash
python -m http.server 5173
```

4. Acesse `http://localhost:5173`.
5. Gere uma chave no painel, salve a `X-API-Key` e use as telas de listar, adicionar, editar e excluir.

Nao abra o `index.html` direto pelo navegador com `file://`, porque chamadas CORS precisam usar `http` ou `https`.

## Publicar na Vercel

1. Suba o projeto para o GitHub.
2. Na Vercel, escolha `Add New Project`.
3. Importe o repositorio.
4. Em `Root Directory`, selecione `frontend`.
5. Deixe `Build Command` vazio.
6. Deixe `Output Directory` vazio ou como `.`.
7. Publique.
8. No painel, troque a URL da API para a URL publica do Spring Boot.

## Publicar no GitHub Pages

O repositorio ja tem o workflow `.github/workflows/deploy-frontend.yml`.

1. Suba o projeto para o GitHub.
2. No GitHub, acesse `Settings > Pages`.
3. Em `Build and deployment`, escolha `GitHub Actions`.
4. Faca um push na branch `main` ou `master`.
5. Aguarde a action `Deploy Frontend`.
6. Use a URL gerada pelo GitHub Pages.
7. No painel, configure a URL publica da API.

## CORS

A API foi ajustada para aceitar chamadas locais, Vercel e GitHub Pages:

- `http://localhost:*`
- `http://127.0.0.1:*`
- `https://*.vercel.app`
- `https://*.github.io`

Se a API ficar em outro dominio, adicione esse dominio na configuracao CORS do Spring Boot.
