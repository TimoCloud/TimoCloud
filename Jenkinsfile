pipeline {
	agent any
	tools {
        	maven '3.8.6'
		jdk '8'
    	}
	stages {
		stage ('Clean Workspace') {
			steps {
				deleteDir()
			}
		}
	    stage ('Clone') {
	    	steps {
	    		checkout scm
	    	}
	        
	    }
	    stage('Build') {
	    	steps {
	        	echo 'Building..'
	        	sh 'mvn clean install'
	        	archiveArtifacts artifacts: 'TimoCloud-Universal/target/TimoCloud.jar', fingerprint: true  
	    	}

	    }
	    stage('Test') {
	    	steps {
	    		echo 'Testing..'
	        	sh 'mvn clean test'
	    	}
	        
	    }
	    stage('Deploy') {
	    	steps {
	    		echo 'Deploying....'
	    	}
	    }
    }
}
