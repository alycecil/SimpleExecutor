package com.acecil.controller

import static org.testng.Assert.*

import org.testng.annotations.*

import com.acecil.util.Enviroment

public class FilePlayerControllerTest {
	def me = new FilePlayerController()

	@Test
	public void test_settings_nothing(){
		def res = me.settings null

		assertNotEquals res, 'success'
	}

	@Test
	public void test_settings_set(){
		def cmd = 'echo'
		def res = me.settings cmd

		assertEquals res, 'success'

		assertEquals me.VLC_COMMAND, cmd
	}

	@Test(dependsOnMethods='test_settings_set')
	public void test_playFile(){
		def cmd = 'echo'
		me.settings cmd

		def videoFile = 'video.movie'
		def res = me.playFile videoFile
		assertTrue res.contains('file://'), "[$res].contains('file://')"
		assertTrue res.contains(videoFile), "[$res].contains([$videoFile])"
	}

	@Test(expectedExceptions=IllegalStateException)
	public void test_playFile_err1(){
		me.playFile null
	}

	@Test(expectedExceptions=IllegalStateException)
	public void test_playFile_err2(){
		me.playFile ' \t\n'
	}

	@Test(dataProvider="data_doSystemCall")
	public void test_doSystemCall(File file){
		def expected = 'Hi, Alice'
		def cmd = "echo $expected"
		if(Enviroment.isWindows()){
			cmd = "cmd /c $cmd"
		}
		def res = me.doSystemCall(cmd, file?.getAbsolutePath())

		assertEquals res, "$expected\n" as String
	}

	@DataProvider(name="data_doSystemCall")
	public Object[][] data_doSystemCall(){
		[//
			[new File('')], //
			[null as File],
		] as Object[][]
	}

	@Test
	public void test_isAlive(){

		def r1 = me.isAlive()
		assertTrue r1.isNumber()

		def r2 = me.isAlive()
		assertTrue r2.isNumber()

		assertTrue ((r1 as Long) <= (r2 as Long), 'time increment must be increasing')
	}
}
