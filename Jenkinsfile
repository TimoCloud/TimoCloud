node {
    deleteDir()
    stage ('Clone') {
        checkout scm
    }
    stage('Build') {
        echo 'Building..'
        sh 'mvn clean install'
        archiveArtifacts artifacts: 'TimoCloud-Universal/target/TimoCloud.jar', fingerprint: true  
    }
    stage('Test') {
        echo 'Testing..'
    }
    stage('Deploy') {
        echo 'Deploying....'
    }
}