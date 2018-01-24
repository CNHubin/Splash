# Splash
导航模块增量更新等功能抽取，方便多应用之间模块迁移

Step 1. Add the JitPack repository to your build file

Add it in your root build.gradle at the end of repositories:

	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
  
Step 2. Add the dependency

	dependencies {
	        compile 'com.github.343906936:Splash:1.0.0'
	}
