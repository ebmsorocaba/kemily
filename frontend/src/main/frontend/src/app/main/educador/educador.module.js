(function() {
  'use strict';

  angular.module('app.educador', []).config(config);

  /** @ngInject */
  function config($stateProvider, msApiProvider, msNavigationServiceProvider) {
    // State
    $stateProvider.state('app.educador', {
      url: '/educador',
      views: {
        'content@app': {
          templateUrl: 'app/main/educador/educador.html',
          controller: 'EducadoresController as vm'
        }
      },
      resolve: {
        User: function($window) {
          return JSON.parse($window.localStorage.getItem("currentUser"));
        },
        authenticate: authenticate,
        Educadores: function(msApi) {
          return msApi.resolve('educadores.educadores@query')
        }
      }
    });

    function authenticate($q, User, $state, $timeout, $mdDialog) {
      if (User != null) {
        // Resolve the promise successfully
        if (User.setor == 'Social' || User.setor == "Desenvolvimento") {
          return $q.when();
        } else {
          var confirm = $mdDialog.alert().title('Não permitido!').textContent('Apenas o setor social pode acessar esse módulo.').ariaLabel('OK').ok('OK')

          $mdDialog.show(confirm).then(function() {});
          return $q.reject();
        }
      } else {
        // The next bit of code is asynchronously tricky.

        $timeout(function() {
          // This code runs after the authentication promise has been rejected.
          // Go to the log-in page
          $window.location.href = '/#/login';
        })

        // Reject the authentication promise to prevent the state from loading
        return $q.reject();
      }
    }

    msApiProvider.register('educadores.educadores', ['/api/educador'])

    // Translation
    // $translatePartialLoaderProvider.addPart('app/main/relatorio');

    // Api
    //msApiProvider.register('sample', ['app/data/sample/sample.json']);

    //Navigation
    // msNavigationServiceProvider.saveItem('social', {
    //     title : 'Social',
    //     group : true,
    //     weight: 8
    // });

    msNavigationServiceProvider.saveItem('social.educador', {
      title: 'Gerenciar Educadores',
      icon: 'icon-school',
      state: 'app.educador',
      weight: 2
    });
  }
})();
