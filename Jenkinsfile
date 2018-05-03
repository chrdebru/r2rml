pipeline {
  agent any
  stages {
    stage('checkout') {
      steps {
        git 'https://github.com/amalic/r2rml.git'
      }
    }
    stage('build') {
      steps {
        sh 'docker build --no-cache -t r2rml .'
      }
    }
  }
}