/*
	see copyright notice in squirrel.h
*/
#include "sqpcheader.h"
//#include "ogre.h"

//SQUnsignedInteger sqMemTotal = 0;

void *sq_vm_malloc(SQUnsignedInteger size)
{	
//	return OGRE_MALLOC(size, MEMCATEGORY_GENERAL);
	//sqMemTotal += size;
	return malloc(size); 
}

// Ogre doesn't have a realloc, so simulate it.
// TODO: ADD REALLOC TO OGRE!
void *sq_vm_realloc(void *p, SQUnsignedInteger oldsize, SQUnsignedInteger size)
{ /*
	void * newBuffer =  sq_vm_malloc(size);

	if(p)
	{
		memcpy(newBuffer,p,std::ios::min(oldsize,size));
		sq_vm_free(p);
	}

	return newBuffer;
	*/
	//sqMemTotal -= oldsize;
	//sqMemTotal += size;
	return realloc(p, size); 
}

void sq_vm_free(void *p, SQUnsignedInteger size)
{
//	OGRE_FREE(p, MEMCATEGORY_GENERAL);
	//sqMemTotal -= size;
	free(p); 
}
