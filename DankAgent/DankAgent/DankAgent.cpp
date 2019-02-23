#define _CRT_SECURE_NO_WARNINGS
#include <jvmti.h>
#include <stdlib.h>
#include <string>
#include <iostream>

jvmtiEnv* jvmti = NULL;

static jvmtiIterationControl JNICALL InstanceSearchCallback(jlong class_tag, jlong size, jlong* tag_ptr, void* userdata)
{
	*tag_ptr = 1337;
	return JVMTI_ITERATION_CONTINUE;
}

extern "C"
{
	JNIEXPORT jobjectArray JNICALL Java_dankscape_nativeinterface_NativeInterface_getAllInstancesOfClass(JNIEnv* env, jclass clazz, jobject clazzToSearch)
	{
		jclass targetClass = (jclass)clazzToSearch;
		if (!targetClass)
		{
			std::cout << "ERROR: Could not find class!" << std::endl;
			return env->NewObjectArray(0, env->FindClass("java/lang/Object"), NULL);
		}
		jint err = jvmti->IterateOverInstancesOfClass(targetClass, JVMTI_HEAP_OBJECT_EITHER, InstanceSearchCallback, NULL);
		jlong tag = 1337;
		jint count = 0;
		jobject* instances = NULL;
		jvmti->GetObjectsWithTags(1, &tag, &count, &instances, NULL);

		if (count >= 0)
		{
			jobjectArray objectArray = env->NewObjectArray(count, env->FindClass("java/lang/Object"), NULL);
			for (int i = 0; i < (int)count; i++)
				env->SetObjectArrayElement(objectArray, i, instances[i]);
			jvmti->Deallocate((unsigned char*)instances);
			return objectArray;
		}
		return env->NewObjectArray(0, env->FindClass("java/lang/Object"), NULL);
	}


	JNIEXPORT jint JNICALL Agent_OnLoad(JavaVM *jvm, char *options, void *reserved)
	{
		std::cout << "Starting JVMTI Agent..." << std::endl;
		jint err = jvm->GetEnv((void**)&jvmti, JVMTI_VERSION_1_2);
		if (err != JNI_OK)
			return err;

		jvmtiCapabilities capabilities;
		memset(&capabilities, 0, sizeof(capabilities));

		capabilities.can_tag_objects = 1;


		jvmti->AddCapabilities(&capabilities);

		return JNI_OK;
	}

	JNIEXPORT jint JNICALL Agent_OnAttach(JavaVM* vm, char* options, void* reserved)
	{
		return JNI_OK;
	}

	JNIEXPORT void JNICALL Agent_OnUnload(JavaVM *vm)
	{
	}

}