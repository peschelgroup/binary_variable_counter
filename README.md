# binary_variable_counter
Usage: The Binary Variable Counter is a program which allows you to measure the change in
state of a proccess.

###Install and Setup
1. Download the project directory from our GitHub page.
	- Inside the ViewSwitcher Directory is a precompiled jar file
	- Alternatively, this program was written in Eclipse, so it can be reimported
	  and recompiled if there are any java version differences.
2. Move the precompiled jar file or the newly compiled jar file to a directory of your choosing
3. Double click the jar file and it should run
   - *NOTE: If you choose to recompile the jar file yourself, make sure to compile the version
	  specific to your operating system*

###How To Use
When the jar file is ran, a gui prompting for a file name, a left and right box, a begin
button, a pause button, and a close button. The program will not run unless the file name text box
has some sort of text inside of it. Once this box is populated, the begin button will start the logging
process. It should be noted that the program always begins logging on the left side.
Once this has started, the pause button will become responsive to clicks. Clicking this
button, or clicking the space bar, will result in the timer located at the bottom of the
interface to pause. This can be continued by pressing the space bar again or by pressing the pause button
once more. To switch between logging on the right and left, pressing the 1 and 2 keys on either the numpad
or number bar at the top of the keyboard will switch to the left and right views respecitively. 

The bar at the top of each table will tell you what each column represents. Switch refers to the number of times 
the value has flipped over to it's other state. Start refers to the time at which the switch happend. Duration
refers to the length of time with which the value was in this state. End refers to the time at which the value 
flipped to it's second state. It should be noted that pressing the 1 or 2 key twice will not change the logger
to the opposite state.

Pressing what was once the "Begin", now the "End" key, will result in the timer ending and all further data
logging to cease. At the bottom of the table, there will be a new row with 3 new columns, the fourth will be empty.
In this new row on both tables, there will be the total number of times the value was in that state, the total
duration that the value stated in that state, and the percentage of time it was in that state in relation to the total 
time that the value was being measured. Pressing the end button will also generate a log file with all measurements
taken and the statistics generated previously. Pressing "Begin" again will start a new log. Note, if the name isn't
changed, it will completely overwrite the file. All generated log files will be storaged in a folder titled "participants"
generated in the same directory as the jar file. This folder can be renamed in the TrackerGUI.java file on line
225. Changing the value of the second parameter will change the name of the folder generated. Pressing the close
button will close out of the program.

###Quick Reference Sheet
	- File Name: Name of the log file generated. Must have something inside of it.
	- Begin/End: Starts and stops the logging process. Will always start on the left side.
	             Only starts if the file name box is populated.
	- Pause/Play: Pauses and plays the timer. Only works when the program has begun.
	- SpaceBar: Pauses and plays timer. Only works when the program has begun.
	- 1/2: Keyboard button to switch views. 1 switches to left, 2 switches to right.
	- Close: Closes out of the program.












