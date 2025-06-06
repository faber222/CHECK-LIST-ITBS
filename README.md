# CHECK-LIST-ITBS

**CHECK-LIST-ITBS** é uma aplicação de desktop desenvolvida em Java Swing, projetada para ser um cliente offline-first para gerenciamento de checklists. A aplicação permite que técnicos de campo utilizem checklists detalhados para procedimentos de instalação e configuração de equipamentos de rede, com a capacidade de sincronizar os dados com um servidor central quando online.

## 🚀 Funcionalidades Principais

* **Operação Offline-First**: A aplicação utiliza um banco de dados SQLite local, permitindo que todas as operações sejam realizadas sem a necessidade de uma conexão constante com a internet.
* **Sincronização Sob Demanda**: Através de uma interface simples, o usuário pode solicitar a sincronização de todos os dados (categorias, checklists e itens) a partir de um banco de dados MySQL remoto.
* **Interface Dinâmica e Reativa**:
    * As categorias de checklists são carregadas dinamicamente a partir do banco de dados e exibidas em uma barra lateral.
    * A seleção de uma categoria abre uma nova aba com os modelos de checklist correspondentes.
    * Ao alternar entre checklists, a aplicação verifica se existem alterações não salvas e solicita confirmação do usuário.
* **Temas Light e Dark**: Suporte para alternância entre temas claro e escuro, utilizando a biblioteca [FlatLaf](https://www.formdev.com/flatlaf/) com arquivos de propriedades customizados.
* **Barra Lateral Retrátil**: A barra de navegação de categorias pode ser expandida ou recolhida para otimizar o espaço de tela.

## 🏛️ Arquitetura

O projeto segue uma arquitetura em camadas bem definida para garantir a separação de responsabilidades, facilitando a manutenção e a escalabilidade.

* **`view`**: Contém todas as classes da interface gráfica (Swing), como `MainWindow`, `SidebarPanel` e `ChecklistViewer`. É responsável por exibir os dados e capturar as interações do usuário.
* **`controller`**: Intermedeia a comunicação entre a `view` e os serviços. Inclui os listeners como `SidebarListener` e `RenewDbListener`.
* **`service`**: Camada responsável pela lógica de negócio e acesso a dados.
    * `LocalDatabaseService`: Gerencia todas as operações de CRUD no banco de dados SQLite local.
    * `RemoteSyncService`: Gerencia a conexão com o servidor MySQL remoto e orquestra o processo de sincronização de dados.
* **`model`**: Representa as entidades de dados da aplicação, como `Categorias`, `Checklist` e `ChecklistItens`.

### Fluxo de Sincronização
1.  O usuário inicia a aplicação, que carrega todos os dados do banco **SQLite local**.
2.  Para atualizar, o usuário acessa `Ajuda > Verificar Sobre Atualizações`.
3.  Uma janela solicita o endereço IP do servidor remoto.
4.  O `RemoteSyncService` se conecta ao **MySQL remoto** e busca todos os dados.
5.  O `LocalDatabaseService` apaga os dados locais antigos e insere os novos dados buscados do servidor.
6.  A interface é atualizada para refletir o novo conjunto de dados.

## 🛠️ Tecnologias e Bibliotecas

* **Linguagem**: Java
* **Interface Gráfica**: Java Swing
* **Tema (Look and Feel)**: [FlatLaf](https://www.formdev.com/flatlaf/) (FlatMac Light/Dark)
* **Banco de Dados Local**: SQLite
* **Banco de Dados Remoto**: MySQL
* **Build Tool**: Gradle (v8.14.1)

## ⚙️ Setup e Execução (Passo a Passo)

Siga estes passos para configurar e executar o projeto em seu ambiente de desenvolvimento.

### 1. Pré-requisitos
* **JDK** (Java Development Kit) 11 ou superior.
* **Gradle** (o projeto utiliza o Gradle Wrapper, então a instalação manual não é estritamente necessária).
* Um servidor **MySQL** acessível na rede.

### 2. Configuração do Banco de Dados Remoto
1.  Execute o script SQL `db/dbremoto.sql` em seu servidor MySQL.
2.  Este script irá:
    * Criar o banco de dados `redes_db`.
    * Criar os usuários `AdminRedes` e `SuporteRedes` (a aplicação utiliza o `SuporteRedes` para leitura).
    * Criar as tabelas `categorias`, `checklists` e `checklist_itens`.
    * Popular as tabelas com dados iniciais.

### 3. Configuração do Banco de Dados Local
O banco de dados local (`dblocal.db`) é um arquivo SQLite. Ele é gerenciado automaticamente pela aplicação. O schema de referência pode ser encontrado em `db/dblocal.sql` ou `app/db/dblocal.sql`. Ao sincronizar, a aplicação irá apagar os dados existentes e preenchê-los com os dados do servidor remoto.

### 4. Build do Projeto
Para compilar todo o código-fonte e gerar os arquivos necessários, execute o seguinte comando na raiz do projeto:
```bash
./gradlew build
```

### 5. Execução da Aplicação
Para iniciar a aplicação, utilize o comando:
```bash
./gradlew run
```

## 📖 Como Usar

1.  **Início**: A aplicação iniciará e carregará as categorias da base de dados local na barra lateral.
2.  **Sincronização**:
    * Vá em `Ajuda > Verificar Sobre Atualizações`.
    * Na janela que surgir, digite o endereço IP do seu servidor MySQL e clique em "Ok".
    * Aguarde a mensagem de confirmação. Após a sincronização, a lista de categorias na barra lateral será atualizada.
3.  **Navegação**:
    * Clique em uma categoria na barra lateral para abrir uma nova aba contendo os checklists daquela categoria.
    * Selecione um checklist na lista à esquerda para ver seus itens à direita.
4.  **Uso do Checklist**:
    * Marque os `JCheckBox` dos itens concluídos.
    * Adicione notas no campo "Observações".
    * Clique no botão **"Copiar"** para enviar uma versão formatada do checklist para a área de transferência.
5.  **Mudar o Tema**:
    * Vá em `Edit > Tema` para alternar entre os modos claro e escuro. A preferência é salva e será aplicada na próxima vez que a aplicação for aberta.

## 📝 Licença

MIT License

Copyright (c) 2025 Faber Bernardo

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
