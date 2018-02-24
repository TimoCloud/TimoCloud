node {
    // Clean workspace before doing anything
    deleteDir()

    try {
        stage ('Clone') {
            checkout scm
        }
        stage ('Build') {
            steps {
                sh 'mvn -Dmaven.test.failure.ignore=true install' 
            }
            post {
                success {

                }
            }
        }
        stage ('Tests') {
            parallel 'static': {
            },
            'unit': {
            },
            'integration': {
            }
        }
        stage ('Deploy') {
        }
    } catch (err) {
        currentBuild.result = 'FAILED'
        throw err
    }
}
