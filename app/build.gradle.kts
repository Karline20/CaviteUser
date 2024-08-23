plugins {
    id ("com.android.application")
    id ("org.jetbrains.kotlin.android")
    id ("kotlin-android")
    id ("kotlin-kapt")
    id ("com.jakewharton.butterknife")
    id ("com.google.gms.google-services")
    //id("com.google.devtools.ksp")
}

android {
    namespace = "coding.legaspi.caviteuser"
    compileSdk = 34

    defaultConfig {
        applicationId = "coding.legaspi.caviteuser"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField ("String","BASE_URL", "\"https://backend-cavitev3.onrender.com/\"")
        //buildConfigField ("String","BASE_URL", "\"https://backend-cavite-production.up.railway.app/\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures{
        dataBinding = true
        viewBinding = true
    }

    kapt {
        generateStubs = true
        correctErrorTypes = true
        useBuildCache = true
    }

}

dependencies {

    implementation ("com.google.android.gms:play-services-maps:18.1.0")
    implementation ("com.google.firebase:firebase-database-ktx:20.2.2")
    implementation ("com.google.firebase:firebase-storage-ktx:20.2.1")
    implementation("com.google.firebase:firebase-auth-ktx:22.1.2")
    implementation ("com.google.android.gms:play-services-maps:18.1.0")
    implementation ("com.google.android.gms:play-services-location:21.0.1")
    implementation ("com.github.prabhat1707:EasyWayLocation:2.4")
    implementation("com.android.volley:volley:1.2.1")
    implementation ("com.akexorcist:google-direction-library:1.2.1")
    implementation("androidx.activity:activity:1.8.0")


    val lifecycle_version = "2.6.1"
    val arch_version = "2.2.0"
    val room_version = "2.5.2"
    val coroutines_version = "1.7.3"
    val dagger_version = "2.47"
    val retrofit_version = "2.9.0"
    val interceptor_version = "4.11.0"
    //ui
    val glide_version = "4.15.1"
    val epoxyVersion = "5.1.3"
    val circleImageView = "3.1.0"
    val rounderImageVersion = "v1.0.1"
    val picassoVersion = "2.8"
    val stylishDialogsVersion = "1.0.0"
    val ratingVersion = "1.0.0"
    val lottieVersion = "3.4.0"
    val imageSlideVersion = "0.1.2"
    val datePickerVersion = "2.0.7"


    // ViewModel
    implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycle_version")
    // LiveData
    implementation ("androidx.lifecycle:lifecycle-livedata-ktx:$lifecycle_version")
    // Saved state module for ViewModel
    implementation ("androidx.lifecycle:lifecycle-viewmodel-savedstate:$lifecycle_version")

    // Annotation processor
    kapt ("androidx.lifecycle:lifecycle-compiler:$lifecycle_version")

    implementation ("androidx.room:room-runtime:$room_version")
    kapt ("androidx.room:room-compiler:$room_version")

    implementation ("androidx.room:room-ktx:$room_version")

    //coroutines
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutines_version")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutines_version")

    //dagger
    implementation ("com.google.dagger:dagger:$dagger_version")
    kapt ("com.google.dagger:dagger-compiler:$dagger_version")

    //retrofit
    implementation ("com.squareup.retrofit2:retrofit:$retrofit_version")
    implementation ("com.squareup.retrofit2:converter-gson:$retrofit_version")
    //okhttp
    implementation ("com.squareup.okhttp3:logging-interceptor:$interceptor_version")

    //ui
    //glide
    implementation ("com.github.bumptech.glide:glide:$glide_version")
    kapt ("com.github.bumptech.glide:compiler:$glide_version")
    implementation( "de.hdodenhof:circleimageview:$circleImageView")
    implementation ("com.airbnb.android:epoxy:$epoxyVersion")
    kapt ("com.airbnb.android:epoxy-processor:$epoxyVersion")
    implementation ("com.airbnb.android:epoxy-databinding:$epoxyVersion")
    implementation ("com.github.mmmelik:RoundedImageView:$rounderImageVersion")
    implementation ("com.squareup.picasso:picasso:$picassoVersion")
    implementation ("com.github.MarsadMaqsood:StylishDialogs:$stylishDialogsVersion")
    implementation ("com.github.denzcoskun:ImageSlideshow:$imageSlideVersion")
    implementation ("com.github.swnishan:materialdatetimepicker:1.0.0")

    implementation ("com.github.ozcanalasalvar.picker:datepicker:$datePickerVersion")
    implementation ("com.github.ozcanalasalvar.picker:wheelview:$datePickerVersion")

    implementation ("com.github.KaelLi1989:NiceRatingBar:$ratingVersion")

    implementation ("com.airbnb.android:lottie:$lottieVersion")
    
    implementation("androidx.core:core-ktx:1.8.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}

