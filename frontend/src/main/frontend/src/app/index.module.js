(function ()
{
    'use strict';

    /**
     * Main module of the Fuse
     */
    angular
        .module('fuse', [

            // angular-mask (ver bower.json)
            'ngMask', 'ui.utils.masks',

            // Core
            'app.core',

            // Navigation
            'app.navigation',

            // Toolbar
            'app.toolbar',

            // Cadastro de Usuários
            'app.usuario',

            // Cadastro de Associados
            'app.associados',

            // Informar pagamento
            'app.oldPagamento',

            'app.pagamento',

            // Relatório financeiro
            'app.relatorio',

            'app.aluno',

            'app.educador',

            'app.projeto',

            'app.relatorioSocial'
        ])
})();
