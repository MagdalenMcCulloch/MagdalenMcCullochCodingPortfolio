/*this program utilised shared memory to quickly and efficently create the mandelbrot fractal. Specifically, 
it uses fork() and has 4 children to compute each quadrent of the image simultaneously. 
to run the program, use WSL and make sure gimp is downloaded
command prompts are (in correct directory):
$ make multi_mandelbrot
$ ./multi_mandelbrot
The image will be saved to the same folder as the program. 
*/ 
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <assert.h>
#include <time.h>
#include <sys/wait.h>
#include <sys/time.h>
#include <sys/shm.h>
#include <sys/ipc.h>
#include "read_ppm.h"
#include <string.h>
#define BUF_SIZE 1024
#define SHM_KEY 0x1234

int main(int argc, char* argv[]) {
  int size = 2000;
  float xmin = -2.0;
  float xmax = 0.47;
  float ymin = -1.12;
  float ymax = 1.12;
  int maxIterations = 1000;
  int numProcesses = 4;

  int opt;
  while ((opt = getopt(argc, argv, ":s:l:r:t:b:p:")) != -1) {
    switch (opt) {
      case 's': size = atoi(optarg); break;
      case 'l': xmin = atof(optarg); break;
      case 'r': xmax = atof(optarg); break;
      case 't': ymax = atof(optarg); break;
      case 'b': ymin = atof(optarg); break;
      case '?': printf("usage: %s -s <size> -l <xmin> -r <xmax> "
        "-b <ymin> -t <ymax> -p <numProcesses>\n", argv[0]); break;
    }
  }
  printf("Generating mandelbrot with size %dx%d\n", size, size);
  printf("  Num processes = %d\n", numProcesses);
  printf("  X range = [%.4f,%.4f]\n", xmin, xmax);
  printf("  Y range = [%.4f,%.4f]\n", ymin, ymax);

  // todo: your code here
  // code from single!!! 
  //#1 calculate and output the number of seconds needed to generate the image
  struct timeval tstart, tend; 
  double timer;
  
  char name [1000]; 
  char timestamp [20]; 
  char sz [10]; 
  strcpy(name, "multi-mandelbrot-"); 
  
  time_t now = time(0);  
  strftime(timestamp,20, "%Y-%m-%d %H:%M:%S", localtime(&now)); 
  sprintf(sz,"%d",size); 
  strcat(name,sz); 
  strcat(name,timestamp);  
  strcat(name,".ppm");
  printf("%s\n",name);


  int shmid, numtimes; 
  struct shmseg *shmp; 
  char *bufptr; 
  int spaceavailable; 
  shmid = shmget(IPC_PRIVATE, sizeof(struct ppm_pixel)*size*size, 0644 | IPC_CREAT); 
  if(shmid == -1){
    perror("Error: cannot initialize shared memory\n");
    exit(1); 
  }
  struct ppm_pixel* buffer = shmat(shmid, NULL, 0);
  if (buffer == (void*)-1){
    perror("Error: cannot access shared memory\n");
    exit(1); 
  } 
  int cstart = 0; 
  int cend = 0;
  int rstart = 0; 
  int rend = 0;

  int numberOfChildren = 4; 
  int isParent = 1; 
  int childNumber; 
  pid_t pid; 

  for(int i = 0; i < numberOfChildren; i++){
    pid = fork(); 
      if(pid == 0){
        if(i == 0){
          cstart = 0; 
          cend = size/2;
          rstart = 0; 
          rend = size/2;
        }
        if(i == 1){
          cstart = size/2; 
          cend = size;
          rstart = 0; 
          rend = size/2;
        }
        if(i == 2){
          cstart = 0; 
          cend = size/2;
          rstart = size/2; 
          rend = size;
        }
        if(i == 3){
          cstart = size/2; 
          cend = size;
          rstart = size/2; 
          rend = size;
        }
        printf("Launched child process: %i\n",pid);
        printf("sub image block: col (%i,%i) to rows (%i,%i)\n",cstart,cend,rstart,rend); 

         float xfrac = 0; 
        float yfrac = 0;
 
         float x0 = 0; 
         float y0 = 0; 

         float x = 0; 
         float y = 0;  
 
         int iter = 0; 
         float xtemp = 0; 
        int rVal = 0; 
        int gVal = 0; 
        int bVal = 0; 
        srand(time(0));
        gettimeofday(&tstart,NULL);
       // generate pallet
        int colorValRed = rand()%225;
        int colorValBlue = rand()%225;
        int colorValGreen = rand()%225;
        //mandelbrot math
       for(int row = rstart; row < rend;row++){ // goes through each row
         for(int col = cstart; col < cend;col++){ //goes through each col 
           float r = row;
           float c = col; 
           float mySize = size; 
            xfrac = r / mySize; 
           yfrac = c / mySize;
      
          x0 = xmin + xfrac * (xmax - xmin); 
          y0 = ymin + yfrac * (ymax -ymin); 
          /* used for testing 
          if((c > 400) && (r == 1)){
              printf(" %f",r); 
              printf(" %f",c);
              printf(" %f",x0); 
              printf(" %f\n",y0); 
              } 
            */ 

          x = 0; 
          y = 0; 
          iter = 0;
          
          while(iter < maxIterations && x*x + y*y < 2*2){
              xtemp = (x*x) - (y*y) + x0;
              y = (2*x*y) + y0;
              x = xtemp;  
              iter++; 
          }
          
          if(iter < maxIterations){ //escaped 
            buffer[(col*size)+row].red = colorValRed + iter ; 
            buffer[(col*size)+row].blue = colorValBlue +iter; 
            buffer[(col*size)+row].green = colorValGreen + iter; 
          }
          else{
            buffer[(col*size)+row].red = 0; 
            buffer[(col*size)+row].blue = 0; 
            buffer[(col*size)+row].green = 0; 
         } 
       }  
     }
     exit(1);
   } 
    buffer[(size*size-1)].red = 0; 
    buffer[(size*size-1)].blue = 0; 
    buffer[(size*size-1)].green = 0; 
      
  }

  for(int z =0; z < numberOfChildren; z++){
    int status; 
    int pid = wait(&status); 
    printf("Child process complete %d\n",pid); 
  }
  struct ppm_pixel **mandelbrot; 
  mandelbrot = malloc(sizeof(struct ppm_pixel)*size*size); 
  for(int i = 0; i < size; i++){
    mandelbrot[i] = &(buffer[i*size]); 
  }
  
  // compute image
  gettimeofday(&tend,NULL);
  write_ppm(name,*mandelbrot,size,size); //why is write in the loops in the assignment description??
  timer = tend.tv_sec - tstart.tv_sec + (tend.tv_usec - tstart.tv_usec)/1.e6;
  printf("Computed mandelbrot set (480x480) in %g seconds\n",timer);  
  free(mandelbrot); 
  free(buffer); 
}
