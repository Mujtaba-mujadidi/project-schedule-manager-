# Team: Yak

# Members:
- Patrick Hainge
- Alin Fulga 
- Mert Acar
- Mujitaba Farooq
 
# Introduction:
This JavaFX application is a schedule generator and is aimed to be used by project managers. However, it may be used by anyone in need of a schedule to aid in managing their tasks. The application takes tasks and people as input and produces a visual schedule in the form of a Gantt chart.  
There are only three steps that need to be completed before a schedule can be generated, the user needs to:
- Add people
- Add tasks
- Add dependencies (if neeeded)

# APIs/Reused Code:

### Google-GSON
"Gson is a Java library that can be used to convert Java Objects into their JSON representation. It can also be used to convert a JSON string to an equivalent Java object. Gson can work with arbitrary Java objects including pre-existing objects that you do not have source-code of."  
GSON is used to provide the saving/loading functionality. It is specifically used to save tasks and people to text files and then to read them back into the system.  
  
GitHub Page: https://github.com/google/gson
  
### JUnit  
"JUnit is a unit testing framework for the Java programming language."  
JUnit is used to aid in writing up unit tests for our application, and to overall ensure that our units work as intended.  
  
Website: http://junit.org/  
  
### Automaton  
"Simple framework which allows the testing of Swing and JavaFX2 applications."  
Automaton is used to write up integration/system tests using our JavaFX GUI. Essentially, it is used to simulate a user accessing our system through our GUI.  
  
GitHub Page: https://github.com/renatoathaydes/Automaton  
