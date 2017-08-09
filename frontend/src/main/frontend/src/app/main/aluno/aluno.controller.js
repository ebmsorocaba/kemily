(function ()
{
    'use strict';

    angular
        .module('app.aluno')
        .controller('AlunoController', AlunoController);

    /** @ngInject */
    function AlunoController($scope, $mdSidenav, User, Alunos, Turmas, msUtils, $mdDialog, $document, api, $window)
    {
      var vm = this;

      // Data
      vm.turmas = Turmas;
      vm.alunos = Alunos;
      //vm.user = User.data;
      vm.listType = 'all';
      vm.listOrder = 'nome';
      vm.listOrderAsc = false;
      vm.selectedAlunos = [];


      // Methods
      vm.openAlunoDialog = openAlunoDialog;
      vm.deleteAlunoConfirm = deleteAlunoConfirm;
      vm.deleteAluno = deleteAluno;
      vm.deleteSelectedAlunos = deleteSelectedAlunos;
      vm.toggleSelectAluno = toggleSelectAluno;
      vm.deselectAlunos = deselectAlunos;
      vm.selectAllAlunos = selectAllAlunos;
      vm.deleteAluno = deleteAluno;
      vm.toggleInArray = msUtils.toggleInArray;
      vm.exists = msUtils.exists;
      //////////

      //vm.currentUser = $window.sessionStorage.getItem("currentUser");

      //console.log("Logado:" + User.nome);
      //console.log("Logado: " + vm.currentUser.nome);

      /**.
       * Open new Aluno dialog
       *
       * @param ev
       * @param contact
       */
      function openAlunoDialog(ev, aluno) {
        $mdDialog.show({
          controller: 'AlunoDialogController',
          controllerAs: 'vm',
          templateUrl: 'app/main/aluno/dialogs/aluno/aluno-dialog.html',
          parent: (angular.element(document.body)),
          targetEvent: ev,
          clickOutsideToClose: false,
          locals: {
            Aluno: aluno,
            User: vm.user,
            Alunos: vm.alunos,
            Turmas: vm.turmas
          }
        });
      }

      /**
       * Delete Aluno Confirm Dialog
       */
      function deleteAlunoConfirm(aluno, ev) {
        var confirm = $mdDialog.confirm()
          .title('Você tem certeza de que deseja apagar este aluno?')
          .htmlContent('<b>' + aluno.nome + ' (' + aluno.ra + ')</b>' + ' será apagado(a).')
          .ariaLabel('apagar contato')
          .targetEvent(ev)
          .ok('Sim')
          .cancel('Cancelar');

        $mdDialog.show(confirm).then(function() {
          deleteAluno(aluno);
          vm.selectedAlunos = [];

        }, function() {
          //console.log('Cancelou');
        });
      }

      /**
       * Delete Aluno
       */
      function deleteAluno(aluno) {

        // TODO Remover também a [formaPgto] do Aluno.

        // Remove o Aluno do BD
        console.log('deleteAluno @ alunos.controller.js');
        api.aluno.getByRa.delete({
            'ra': aluno.ra
          },
          // Sucesso
          function(response) {
            console.log(response);
          },
          // Erro
          function(response) {
            console.error(response);
          }
        );

        // Remove a da lista na tela a linha deste Aluno
        vm.alunos.splice(vm.alunos.indexOf(aluno), 1);
      }

      /**
       * Delete Selected Alunos
       */
      function deleteSelectedAlunos(ev) {
        var confirm = $mdDialog.confirm()
          .title('Você tem certeza de que deseja apagar os alunos selecionados?')
          .htmlContent('<b>' + vm.selectedAlunos.length + ' selecionado(s)</b>' + ' será(ão) apagado(s).')
          .ariaLabel('apagar contatos')
          .targetEvent(ev)
          .ok('Sim')
          .cancel('Cancelar');

        $mdDialog.show(confirm).then(function() {

          vm.selectedAlunos.forEach(function(aluno) {
            deleteAluno(aluno);
          });

          vm.selectedAlunos = [];

        });

      }

      /**
       * Toggle selected status of the aluno
       *
       * @param aluno
       * @param event
       */
      function toggleSelectAluno(aluno, event) {
        if (event) {
          event.stopPropagation();
        }

        if (vm.selectedAlunos.indexOf(aluno) > -1) {
          vm.selectedAlunos.splice(vm.selectedAlunos.indexOf(aluno), 1);
        } else {
          vm.selectedAlunos.push(aluno);
        }
      }

      /**
       * Deselect alunos
       */
      function deselectAlunos() {
        vm.selectedAlunos = [];
      }

      /**
       * Sselect all alunos
       */
      function selectAllAlunos() {
        vm.selectedAlunos = $scope.filteredAlunos;
      }
    }


})();
