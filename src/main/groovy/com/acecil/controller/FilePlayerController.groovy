package com.acecil.controller;

import groovy.transform.CompileStatic

import org.springframework.boot.*
import org.springframework.boot.autoconfigure.*
import org.springframework.stereotype.*
import org.springframework.web.bind.annotation.*

import com.acecil.util.Enviroment

@Controller
@EnableAutoConfiguration
@CompileStatic
public class FilePlayerController {
	String VLC_COMMAND = "vlc"

	public static final String PARAM_VLC_SETS_VLC_COMMAND = "Param vlc sets VLC Command"

	@RequestMapping("/settings")
	@ResponseBody
	String settings(@RequestParam(name = 'vlc', required = false) String vlcCommand) {
		def didSomething = false;
		
		if(vlcCommand!=null && !vlcCommand.isAllWhitespace()){
			VLC_COMMAND = vlcCommand
			didSomething = true;
		}


		if(!didSomething){
			return PARAM_VLC_SETS_VLC_COMMAND
		}
		
		return 'success'
	}

	@RequestMapping("/play")
	@ResponseBody
	String playFile(@RequestParam(name = 'filename') String filename) {
		if(filename == null || filename.isAllWhitespace()){
			throw new IllegalStateException('bad file name')
		}

		filename = filename.trim()

		if(!filename.contains("://")){
			def defaultPath = new File(filename).getAbsolutePath()
			filename = "file://$defaultPath"
		}

		if(Enviroment.isWindows()){
			return doSystemCall("cmd /c $VLC_COMMAND $filename", null)
		}else{
			return doSystemCall("$VLC_COMMAND $filename", null)
		}
	}

	@RequestMapping("/do")
	@ResponseBody
	String doSystemCall(@RequestParam(name = 'command') String command,
			@RequestParam(name = 'directory', required = false) String directory
	) {
		println "Running command [${command}] at [${directory}]"
		Runtime r = Runtime.getRuntime();
		Process p = r.exec(command, null, directory!=null?new File(directory):null);
		p.waitFor();
		BufferedReader b = new BufferedReader(new InputStreamReader(p.getInputStream()));
		String line = "";
		def builder = new StringBuilder(1000);

		while ((line = b.readLine()) != null) {
			builder.append("$line\n")
		}

		b.close();

		println "Done with command [${command}] at [${directory}]"
		
		def res = builder.toString()
		if(res == null || res.isAllWhitespace()){
			res = 'Done'
		}
		return res
	}

	@RequestMapping("/alive")
	@ResponseBody
	String isAlive() {
		return "${System.currentTimeMillis()}";
	}

	public static void main(String[] args) throws Exception {
		SpringApplication.run(FilePlayerController.class, args);
	}
}
