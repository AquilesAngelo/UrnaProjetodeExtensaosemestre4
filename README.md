# Urna Cidadã – Simulador educativo de urna eletrônica

Simulador em Java (Swing) com votação sequencial para dois cargos (Vereador – proporcional, 3 dígitos;
Prefeito – majoritário, 2 dígitos) e persistência no Cloud Firestore (Firebase).
Candidatos e partidos fictícios, sem vínculo com candidatos reais.

## Executar sem instalar nada (Windows)
1. Baixe o `UrnaV3.zip` na aba **Releases** deste repositório e extraia.
2. Copie o arquivo `firebase-chave.json` (fornecido separadamente) para a pasta `UrnaV3`, ao lado do `UrnaV3.exe`.
3. Dê dois cliques em `UrnaV3.exe`.

Sem o `firebase-chave.json`, a urna funciona em modo local (os votos não são enviados à nuvem).

## Mesário
Menu **Mesário → Encerrar votação** (senha definida em `UrnaFrame.SENHA_MESARIO`).

## Desenvolvimento
- Abrir o projeto no IntelliJ (Maven). Executar `urna.Main`.
- Fotos dos candidatos em `fotos/<número>.jpg` ou `.png`.
- A chave do Firebase **não é versionada** (`firebase-chave.json` está no `.gitignore`).

## Estrutura
`Candidato` (abstrata) → `CandidatoProporcional`, `CandidatoMajoritario` · `Cargo` · `Urna` ·
`VotoRepositorio` → `VotoRepositorioFirestore`, `VotoRepositorioLocal` · `UrnaFrame` (interface Swing).
