node {
    deleteDir()
    stage ('Clone') {
        checkout scm
    }
    stage('Build') {
        echo 'Building..'
        sh '''
           echo "PATH = ${PATH}"
           echo "M2_HOME = ${M2_HOME}"
        ''' 
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