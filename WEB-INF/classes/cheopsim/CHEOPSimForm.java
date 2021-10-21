package cheopsim;

// Import required java libraries
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.sql.*;

import javax.servlet.annotation.MultipartConfig;


// Extend HttpServlet class
@MultipartConfig
public class CHEOPSimForm extends HttpServlet {
 
    //Define output filenames
    public static String xmlFilename = "runCHEOPSim.xml";
    public static String starFilename = "mystars.txt";
    private String mpsFilename;
    private String userSEDFilename;
    private String userFluxFilename;
    private String orbitFilename;
    private String psfFilename;
    private String throughputFilename;
    private String qeFilename;
    private String jitterFilename;
    private String telescopeTemperatureFilename;
    private String strayLightFilename;
    private String flatFieldFilename;

    public void doPost(HttpServletRequest request, HttpServletResponse response)
	throws ServletException, IOException {

	mpsFilename = "null";
	userFluxFilename = "userFlux.txt";
	userSEDFilename = "UserSED.fits";
	orbitFilename = "UserOrbit.fits";
	psfFilename = "myPSF.fits";
	throughputFilename = "throughput.txt";
	qeFilename = "qe.txt";
	jitterFilename = "jitter.txt";
	telescopeTemperatureFilename = "telescopeTemperature.txt";
	strayLightFilename = "strayLight.txt";
	flatFieldFilename = "flatField.fits";
	
	//Set the "content type" header of the response
	response.setContentType("text/html");

	//Get the response's PrintWriter to return text to the client.
        PrintWriter out = response.getWriter();

	//Write uploaded MPS_PRE_Visits file to disk (if provided)
	if (request.getParameter("visitFileExists").equals("true")) {
	    //Read in the uploaded MPS_PRE_Visits file and write it to disk
	    try {
		Part filePart = request.getPart("visitFile");
		mpsFilename = getFileName(filePart);
		filePart.write(this.getServletContext().getRealPath(mpsFilename));
	    } catch(Exception e) {
		out.println("Error reading/writing uploaded MPS_PRE_Visits or Observation Request file");
		e.printStackTrace(out);
	    }
	}
	
	if (request.getParameter("doForegroundStars") != null &&
	    request.getParameter("starInputMethod").equals("file")) {

	    //Read star input from file
	    try {
		Part filePart = request.getPart("starFile");
		String filename = getFileName(filePart);
		if (filename.endsWith(".fits")) starFilename = filename;
		filePart.write(this.getServletContext().getRealPath(starFilename));
	    } catch(Exception e) {
		out.println("Error reading / writing the uploaded star file");
		e.printStackTrace(out);
	    }

	} else {

	    //Read star input from form
	    try {
		//Open the file for writing the star data
		FileWriter starFile = new FileWriter(this.getServletContext().getRealPath(starFilename));
		PrintWriter toStarFile = new PrintWriter(starFile);

		//Get the form data and write it to the star file
		if (request.getParameter("doForegroundStars") != null &&
		    request.getParameter("starInputMethod").equals("form")) {
		    writeStars(request,toStarFile);
		}

		//Close the file.
		starFile.close();

	    } catch(IOException e) {
		out.println("Error opening or writing to star file");
		e.printStackTrace(out);
	    }

	}

	//Write uploaded user SED file to disk (if provided)
	if (request.getPart("UserSEDfile").getSize()>0) {
	    //Read in the uploaded user SED file and write it to disk
	    try {
		Part filePart = request.getPart("UserSEDfile");
		filePart.write(this.getServletContext().getRealPath(userSEDFilename));
	    } catch(Exception e) {
		out.println("Error reading/writing uploaded SED file");
		e.printStackTrace(out);
	    }
	} else {
	    userSEDFilename = "";
	}

	//Write uploaded user Orbit file to disk (if provided)
	if (request.getParameter("orbitInputMethod").equals("upload")) {
	    //Read in the uploaded orbit file and write it to disk
	    try {
		Part filePart = request.getPart("orbitUploadFile");
		filePart.write(this.getServletContext().getRealPath(orbitFilename));
	    } catch(Exception e) {
		out.println("Error reading/writing uploaded orbit file");
		e.printStackTrace(out);
	    }
	} else {
	    orbitFilename = request.getParameter("orbitFilename");
	}
	
	//Write uploaded throughput file to disk (if provided)
	if (request.getParameter("throughputInputMethod").equals("upload")) {
	    //Read in the uploaded throughput file and write it to disk
	    try {
		Part filePart = request.getPart("throughputUploadFile");
		filePart.write(this.getServletContext().getRealPath(throughputFilename));
	    } catch(Exception e) {
		out.println("Error reading/writing uploaded throughput file");
		e.printStackTrace(out);
	    }
	} else {
	    throughputFilename = request.getParameter("throughputFilename");
	}
	
	//Write uploaded qe file to disk (if provided)
	if (request.getParameter("qeInputMethod").equals("upload")) {
	    //Read in the uploaded qe file and write it to disk
	    try {
		Part filePart = request.getPart("qeUploadFile");
		filePart.write(this.getServletContext().getRealPath(qeFilename));
	    } catch(Exception e) {
		out.println("Error reading/writing uploaded QE file");
		e.printStackTrace(out);
	    }
	} else {
	    qeFilename = request.getParameter("QEFilename");
	}
	
	//Write uploaded jitter file to disk (if provided)
	if (request.getParameter("jitterInputMethod").equals("upload")) {
	    //Read in the uploaded jitter file and write it to disk
	    try {
		Part filePart = request.getPart("jitterUploadFile");
		filePart.write(this.getServletContext().getRealPath("jitter.txt"));
		jitterFilename = "jitter.txt";
	    } catch(Exception e) {
		out.println("Error reading/writing uploaded jitter file");
		e.printStackTrace(out);
	    }
	} else {
	    jitterFilename = request.getParameter("jitterFilename");
	}
	
	//Write uploaded user flux time series file to disk (if provided)
	if (request.getParameter("runUserFluxModifier_star0")!=null) {
	    //Read in the uploaded user flux time series file and write it to disk
	    try {
		Part filePart = request.getPart("userFluxUploadFile");
		filePart.write(this.getServletContext().getRealPath(userFluxFilename));
	    } catch(Exception e) {
		out.println("Error reading/writing uploaded user flux time series file");
		e.printStackTrace(out);
	    }
	}
	
	//Write uploaded telescope temperature file to disk (if provided)
	if (request.getParameter("telescopeTemperatureMethod").equals("upload")) {
	    //Read in the uploaded telescope temperature file and write it to disk
	    try {
		Part filePart = request.getPart("telescopeTemperatureUploadFile");
		filePart.write(this.getServletContext().getRealPath(telescopeTemperatureFilename));
	    } catch(Exception e) {
		out.println("Error reading/writing uploaded telescope temperature file");
		e.printStackTrace(out);
	    }
	} else {
	    telescopeTemperatureFilename = request.getParameter("telescopeTemperatureFilename");
	}
	
	//Write uploaded stray light file to disk (if provided)
	if (request.getParameter("strayLightInputMethod").equals("upload")) {
	    //Read in the uploaded stray light file and write it to disk
	    try {
		Part filePart = request.getPart("strayLightUploadFile");
		filePart.write(this.getServletContext().getRealPath(strayLightFilename));
	    } catch(Exception e) {
		out.println("Error reading/writing uploaded stray light file");
		e.printStackTrace(out);
	    }
	} else if (request.getParameter("strayLightInputMethod").equals("visitConstraints")) {
	    strayLightFilename = mpsFilename;
	} else {
	    strayLightFilename = request.getParameter("strayLightFile");
	}
	
	//Write uploaded flat field file to disk (if provided)
	if (request.getParameter("flatFieldMethod").equals("upload")) {
	    //Read in the uploaded flat field file and write it to disk
	    try {
		Part filePart = request.getPart("flatFieldUploadFile");
		filePart.write(this.getServletContext().getRealPath(flatFieldFilename));
	    } catch(Exception e) {
		out.println("Error reading/writing uploaded flat field file");
		e.printStackTrace(out);
	    }
	} else {
	    flatFieldFilename = request.getParameter("flatFieldFilename");
	}
	
	// if (request.getParameter("psfInputMethod").equals("file")) {

	//     //Read PSF input from file
	//     try {
	//     	Part filePart = request.getPart("psfFile");
	// 	filePart.write(this.getServletContext().getRealPath(psfFilename));
	//     } catch(Exception e) {
	//     	out.println("Error reading PSF file");
	//     	e.printStackTrace(out);
	//     }

	// } else {
	if (request.getParameter("PsfType").equals("monochromatic")) {
	    psfFilename = request.getParameter("monochromaticPSF_filename");
	} else {
	    psfFilename = request.getParameter("whitePSF_filename");
	}
	// }

	//Write the form data to an xml file
        try {
            //Open the file for writing the parameter xml file.
            FileWriter xmlFile = new FileWriter(this.getServletContext().getRealPath(xmlFilename));
            PrintWriter toXmlFile = new PrintWriter(xmlFile);

	    //Get the form data and write it to the xml file
	    writeXML(request,toXmlFile);

	    //Close the file.
            xmlFile.close();

        } catch(IOException e) {
            out.println("Error opening or writing to XML file");
            e.printStackTrace(out);
        }

        //Define the command line job script to create the output zip file containing the configuration and execute it
        configureCHEOPSim(request,out);
	
	//Provide feedback to client
	out.println("<html>");
	out.println("<style>");
	out.println("body {");
	out.println("  font-family: Helvetica, Arial, sans-serif;");
	out.println("  font-size: 14px;");
	out.println("}");
	out.println("</style>");
	out.println("<title>CHEOPSim configuration</title>");
	out.println("<center><br><br><br><h3>CHEOPSim job configuration successful</h3>");
	
	out.println("<form action=\"DownloadXml\" method=\"get\">");
	out.println("<p>The configuration can be downloaded here: <button TYPE=\"Submit\">Download</button>");
        out.println("<p><button TYPE=\"button\" onclick=\"goBack()\">Back to form</button></center>");
	out.println("</form>");

	out.println("<script language=\"JavaScript\">");
	out.println("function goBack() {");
	out.println("    window.history.back()");
	out.println("}");
	out.println("</script>");

        // Close the writer
	out.close();
    }

    private String getFileName(final Part part) {
	final String partHeader = part.getHeader("content-disposition");
	for (String content : part.getHeader("content-disposition").split(";")) {
	    if (content.trim().startsWith("filename")) {
		return content.substring(
					 content.indexOf('=') + 1).trim().replace("\"", "");
	    }
	}
	return null;
    }

    public void configureCHEOPSim(HttpServletRequest request, PrintWriter out) {

        String webDir = this.getServletContext().getRealPath("/");

        try {
            //Write the job configuration script
            FileWriter jobConfigFile = new FileWriter(this.getServletContext().getRealPath("config.sh"));
            PrintWriter jobConfigWriter = new PrintWriter(jobConfigFile);

            jobConfigWriter.println("#!/bin/bash");
            jobConfigWriter.println("cd "+webDir);
            jobConfigWriter.println("rm configuration.zip; mkdir configuration");
            jobConfigWriter.println("mv "+mpsFilename+" "+xmlFilename+" "+starFilename+" "+throughputFilename+" "+qeFilename+" "+userSEDFilename+" "+userFluxFilename+" "+orbitFilename+" "+jitterFilename+" "+telescopeTemperatureFilename+" "+strayLightFilename+" "+flatFieldFilename+" configuration");
            jobConfigWriter.println("zip -r configuration.zip configuration; rm -r configuration");
            jobConfigWriter.close();

        } catch(IOException e) {
            out.println("Error creating job configuration script file");
            e.printStackTrace(out);
        }

        //Execute the job configuration script
        try {
            String[] command = {"/bin/sh", "-c", "cd "+webDir+"; chmod +x config.sh; ./config.sh; rm config.sh"};
            Process p1 = Runtime.getRuntime().exec(command);
            p1.waitFor();
        } catch (IOException | InterruptedException e) {
            out.println("Error executing job configuration script");
            e.printStackTrace(out);
        }

    }

    public void writeStars(HttpServletRequest request, PrintWriter toFile)
    {
	for (int i=0; i<Integer.parseInt(request.getParameter("nStars")); i++) {
	    toFile.print(request.getParameter("star"+i+"_RA")+" ");
	    toFile.print(request.getParameter("star"+i+"_Dec")+" ");
	    toFile.print(request.getParameter("star"+i+"_Mag")+" ");
	    toFile.println(request.getParameter("star"+i+"_SpecType"));
	}
    }


    public void writeXML(HttpServletRequest request, PrintWriter toFile)
    {

	//Generate list of modules
	ArrayList<String> modules = new ArrayList<String>();
	if (request.getParameter("runStarProducer")!=null) modules.add("StarProducer");
	for (int i=0; i<3; i++) {
	    if (request.getParameter("runTransitFluxModulator_star"+Integer.toString(i))!=null) {
		modules.add("TransitFluxModulator_star"+Integer.toString(i));
	    }
	}
	if (request.getParameter("runStellarNoiseFluxModulator_star0")!=null) modules.add("StellarNoiseFluxModulator_star0");
	if (request.getParameter("runStellarVariationFluxModulator_star0")!=null) modules.add("StellarVariationFluxModulator_star0");
	if (request.getParameter("runUserFluxModifier_star0")!=null) modules.add("UserFluxModifier_star0");
	if (request.getParameter("runJitterProducer")!=null) modules.add("JitterProducer");
	if (request.getParameter("runOrbitSimulator")!=null) modules.add("OrbitSimulator");
	if (request.getParameter("runFocalPlaneGenerator")!=null) modules.add("FocalPlaneGenerator");
	if (request.getParameter("runZodiacalLightGenerator")!=null) modules.add("ZodiacalLightGenerator");
	if (request.getParameter("runStrayLightGenerator")!=null) modules.add("StrayLightGenerator");
	if (request.getParameter("runHaloGenerator")!=null) modules.add("HaloGenerator");
	if (request.getParameter("runPSFGenerator")!=null) modules.add("PSFGenerator");
	if (request.getParameter("runGlobalThroughputGenerator")!=null) modules.add("GlobalThroughputGenerator");
	if (request.getParameter("runFlatFieldGenerator")!=null) modules.add("FlatFieldGenerator");
	if (request.getParameter("runDarkCurrentGenerator")!=null) modules.add("DarkCurrentGenerator");
	if (request.getParameter("runFrameTransferSmearer")!=null) modules.add("FrameTransferSmearer");
	if (request.getParameter("runPhotonNoiseGenerator")!=null) modules.add("PhotonNoiseGenerator");
	if (request.getParameter("runCosmicRayGenerator")!=null) modules.add("CosmicRayGenerator");
	if (request.getParameter("runFullWellSimulator")!=null) modules.add("FullWellSimulator");
	if (request.getParameter("runChargeTransferSimulator")!=null) modules.add("ChargeTransferSimulator");
	if (request.getParameter("runBiasGenerator")!=null) modules.add("BiasGenerator");
	if (request.getParameter("runImageWriter")!=null) modules.add("ImageWriter");
	if (request.getParameter("runHKWriter")!=null) modules.add("HKWriter");
	if (request.getParameter("runStarProducer")!=null) modules.add("DataReduction");
	if (request.getParameter("runExampleModule")!=null) modules.add("ExampleModule");

	//Generate the modulesToRun string
	Iterator moduleItr = modules.iterator();
	String modulesToRun="";
	int count=0;
	while(moduleItr.hasNext()) {
	    if (count!=0) modulesToRun+=",";
	    modulesToRun+=moduleItr.next().toString();
	    count++;
	}

	String fovRadius = "759";
	//Define reduced values for FOV radius only if default subarray is used
	if (request.getParameter("imageSizeRadio").equals("subArray") &&
	    request.getParameter("subArrayXDim").equals("200") && request.getParameter("subArrayYDim").equals("200") &&
	    request.getParameter("subArrayXOffset").equals("412") && request.getParameter("subArrayYOffset").equals("412")) {
	    fovRadius = "177";
	    //Increase FOV radius to include stars outside the sub-array when frame transfer smearing or end of life CTI are switched on
	    //so that trails/tails of stars and hot pixels outside the subarray extend into the sub-array
	    if (request.getParameter("runFrameTransferSmearer")!=null ||
		(request.getParameter("runChargeTransferSimulator")!=null && request.getParameter("stageOfLife").equals("end"))) {
		fovRadius = "551";
	    }
	}
	
	Boolean telescopeTemperatureFromFile = (request.getParameter("telescopeTemperatureMethod").equals("file") ? true : false);
	Boolean telescopeTemperatureFromUpload = (request.getParameter("telescopeTemperatureMethod").equals("upload") ? true : false);
	
	String manualHotPixelX = "";
	String manualHotPixelY = "";
	String manualHotPixelRate = "";
	String manualHotPixelIsTelegraphic = "";
	for (int i=0; i<Integer.parseInt(request.getParameter("nManualHotPixels")); i++) {
	    if (i!=0) {
		manualHotPixelX += ",";
		manualHotPixelY += ",";
		manualHotPixelRate += ",";
		manualHotPixelIsTelegraphic += ",";
	    }
	    manualHotPixelX += request.getParameter("manualHotPixel"+i+"_X");
	    manualHotPixelY += request.getParameter("manualHotPixel"+i+"_Y");
	    manualHotPixelRate += request.getParameter("manualHotPixel"+i+"_rate");
	    manualHotPixelIsTelegraphic += (request.getParameter("manualHotPixel"+i+"_isTelegraphic")!=null ? "T" : "F");
	}
	
	toFile.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
	toFile.println("");
	toFile.println("<program_params xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"");
	toFile.println("	xsi:noNamespaceSchemaLocation=\"program_params_schema.xsd\">");

	toFile.println("");
	toFile.println("<param>");
	toFile.println("	<name>MPSFilename</name>");
	toFile.println("	<value>"+mpsFilename+"</value>");
	toFile.println("	<help>Filename for MPS Visit information, format: MPS_PRE_Visits.fsd</help>");
	toFile.println("</param>");
	toFile.println("");
	toFile.println("<param>");
	toFile.println("	<name>startTime</name>");
	toFile.println("	<value>"+request.getParameter("startTime")+"</value>");
	toFile.println("	<help>Start date and time of the simulation (YYYY-MM-DD HH:MM:SS[.SSS])</help>");
	toFile.println("</param>");
	toFile.println("");
	toFile.println("<param>");
	toFile.println("	<name>numberOfStackedImages</name>");
	toFile.println("	<value>"+request.getParameter("numberOfStackedImages")+"</value>");
	toFile.println("	<type>int</type>");
	toFile.println("	<help>Number of stacked images to produce.  Simulation duration is numberOfStackedImages*exposuresPerStack*exposureTime.</help>");
	toFile.println("	<min>1</min>");
	toFile.println("</param>");
	toFile.println("");
	toFile.println("<param>");
	toFile.println("	<name>exposuresPerStack</name>");
	toFile.println("	<value>"+request.getParameter("exposuresPerStack")+"</value>");
	toFile.println("	<type>int</type>");
	toFile.println("	<help>Number of exposures per stack</help>");
	toFile.println("	<min>1</min>");
	toFile.println("	<max>60</max>");
	toFile.println("</param>");
	toFile.println("");
	toFile.println("<param>");
	toFile.println("	<name>exposureTime</name>");
	toFile.println("	<value>"+request.getParameter("exposureTime")+"</value>");
	toFile.println("	<type>double</type>");
	toFile.println("	<help>Exposure time in seconds</help>");
	toFile.println("	<min>0.</min>");
	toFile.println("	<max>600.</max>");
	toFile.println("</param>");
	toFile.println("");
	toFile.println("<param>");
	toFile.println("	<name>repetitionPeriod</name>");
	toFile.println("	<value>"+request.getParameter("repetitionPeriod")+"</value>");
	toFile.println("	<type>double</type>");
	toFile.println("	<help>Repetition period in seconds</help>");
	toFile.println("	<min>0.</min>");
	toFile.println("	<max>600.</max>");
	toFile.println("</param>");
	toFile.println("");
	toFile.println("<param>");
	toFile.println("	<name>maxImagesPerCube</name>");
	toFile.println("	<value>"+request.getParameter("maxImagesPerCube")+"</value>");
	toFile.println("	<type>int</type>");
	toFile.println("	<help>Maximum number of images per image cube</help>");
	toFile.println("	<min>1</min>");
	toFile.println("</param>");
	toFile.println("");
	toFile.println("<param>");
	toFile.println("	<name>doFullFrame</name>");
	toFile.println("	<value>"+(request.getParameter("doFullFrame")!=null ? true : false)+"</value>");
	toFile.println("	<type>bool</type>");
	toFile.println("	<help>Set to true to output a fits file containing a full frame image corresponding to the first exposure of the simulation</help>");
	toFile.println("</param>");
	toFile.println("");
	toFile.println("<param>");
	toFile.println("	<name>pointingRA</name>");
	toFile.println("	<value>"+request.getParameter("pointingRA")+"</value>");
	toFile.println("	<help>Pointing direction Right Ascension in arcseconds</help>");
	toFile.println("</param>");
	toFile.println("");
	toFile.println("<param>");
	toFile.println("	<name>pointingDec</name>");
	toFile.println("	<value>"+request.getParameter("pointingDec")+"</value>");
	toFile.println("	<help>Pointing direction declination in arcseconds</help>");
	toFile.println("</param>");
	toFile.println("");
	toFile.println("<param>");
	toFile.println("	<name>obsId</name>");
	toFile.println("	<value>"+(request.getParameter("obsId").equals("") ? -1 : request.getParameter("obsId"))+"</value>");
	toFile.println("	<type>int</type>");
	toFile.println("	<help>Observation ID</help>");
	toFile.println("</param>");
	toFile.println("");
	toFile.println("<param>");
	toFile.println("	<name>applyThroughput</name>");
	toFile.println("	<value>"+(request.getParameter("applyThroughput")!=null ? true : false)+"</value>");
	toFile.println("	<type>bool</type>");
	toFile.println("	<help>Switch off to set the optical throughput to 100% for the full wavelength range</help>");
	toFile.println("</param>");
	toFile.println("");
	toFile.println("<param>");
	toFile.println("	<name>throughputFilename</name>");
	toFile.println("	<value>"+throughputFilename+"</value>");
	toFile.println("	<help>filename for telescope throughput vs wavelength, format: REF_APP_Throughput.fsd</help>");
	toFile.println("</param>");
	toFile.println("");
	toFile.println("<param>");
	toFile.println("	<name>applyQE</name>");
	toFile.println("	<value>"+(request.getParameter("applyQE")!=null ? true : false)+"</value>");
	toFile.println("	<type>bool</type>");
	toFile.println("	<help>Switch off to apply the flat field only without wavelength dependent quantum efficiency</help>");
	toFile.println("</param>");
	toFile.println("");
	toFile.println("<param>");
	toFile.println("	<name>QEFilename</name>");
	toFile.println("	<value>"+qeFilename+"</value>");
	toFile.println("	<help>filename for quantum efficiency vs wavelength, format: REF_APP_QE.fsd</help>");
	toFile.println("</param>");
	toFile.println("");
	toFile.println("<param>");
	toFile.println("	<name>SEDFilename</name>");
	toFile.println("	<value>"+request.getParameter("sedFilename")+"</value>");
	toFile.println("	<help>filename for SED reference file, format: REF_APP_SEDTeff.fsd. Any string not containing REF_APP_SEDTeff will result in a black body being used instead.</help>");
	toFile.println("</param>");
	toFile.println("");
	toFile.println("<param>");
	toFile.println("	<name>targetSEDFilename</name>");
	toFile.println("	<value>"+userSEDFilename+"</value>");
	toFile.println("	<help>filename for user provided SED fileto be used for the target star. Format: FITS table containing columns named WAVELENGHTH and FLUX. Units of WAVELENGTH must be Angstroms and the wavelength range must cover 3300-11000 Angstroms.</help>");
	toFile.println("</param>");
	toFile.println("");
	toFile.println("<param>");
	toFile.println("	<name>gainCorrectionFilename</name>");
	toFile.println("	<value>"+request.getParameter("gainCorrectionFilename")+"</value>");
	toFile.println("	<help>Filename for the REF_APP_GainCorrection fits file containing the parameters for the gain correction formula as a function of bias voltage values</help>");
	toFile.println("</param>");
	toFile.println("");
	toFile.println("<param>");
	toFile.println("	<name>radius_barycentre</name>");
	toFile.println("	<value>35.</value>");
	toFile.println("	<type>double</type>");
	toFile.println("	<help>Radius of circle centred on image centre used to determine PSF barycentre</help>");
	toFile.println("</param>");
	toFile.println("");
	toFile.println("<param>");
	toFile.println("	<name>radius_bkgInner</name>");
	toFile.println("	<value>"+request.getParameter("radius_bkgInner")+"</value>");
	toFile.println("	<type>double</type>");
	toFile.println("	<help>Inner radius of annulus centred on PSF barycentre used to evaluate the background</help>");
	toFile.println("</param>");
	toFile.println("");
	toFile.println("<param>");
	toFile.println("	<name>radius_bkgOuter</name>");
	toFile.println("	<value>"+request.getParameter("radius_bkgOuter")+"</value>");
	toFile.println("	<type>double</type>");
	toFile.println("	<help>Outer radius of annulus centred on PSF barycentre used to evaluate the background</help>");
	toFile.println("</param>");
	toFile.println("");
	toFile.println("<param>");
	toFile.println("	<name>radius_psf</name>");
	toFile.println("	<value>"+request.getParameter("radius_psf")+"</value>");
	toFile.println("	<type>double</type>");
	toFile.println("	<help>Radius of circle centred on PSF barycentre used to extract the signal flux</help>");
	toFile.println("</param>");
	toFile.println("");
	toFile.println("<param>");
	toFile.println("	<name>subtractBackground</name>");
	toFile.println("	<value>"+(request.getParameter("subtractBackground")!=null ? true : false)+"</value>");
	toFile.println("	<type>bool</type>");
	toFile.println("	<help>Set to true to perform background subtraction during light curve extraction</help>");
	toFile.println("</param>");
	toFile.println("");
	toFile.println("<param>");
	toFile.println("	<name>updateDatabase</name>");
	toFile.println("	<value>false</value>");
	toFile.println("	<type>bool</type>");
	toFile.println("	<help>Set to true to update the CHEOPSim job submission database</help>");
	toFile.println("</param>");
	toFile.println("");
	toFile.println("<param>");
	toFile.println("	<name>databaseServer</name>");
	toFile.println("	<value>local</value>");
	toFile.println("	<help>Name of server for CHEOPSim job submission database</help>");
	toFile.println("</param>");
	toFile.println("");
	toFile.println("<param>");
	toFile.println("	<name>modulesToRun</name>");
	toFile.println("	<value>"+modulesToRun+"</value>");
	toFile.println("	<help>Comma separated list of modules to be run</help>");
	toFile.println("</param>");
	toFile.println("");
	toFile.println("<param>");
	toFile.println("	<name>programType</name>");
	toFile.println("	<value>"+request.getParameter("programType")+"</value>");
	toFile.println("	<type>int</type>");
	toFile.println("	<help>Program Type</help>");
	toFile.println("</param>");
	toFile.println("");
	toFile.println("<param>");
	toFile.println("	<name>piName</name>");
	toFile.println("	<value>"+request.getParameter("piName")+"</value>");
	toFile.println("	<help>Name of Principal Investigator</help>");
	toFile.println("</param>");
	toFile.println("");
	toFile.println("<param>");
	toFile.println("	<name>piUid</name>");
	toFile.println("	<value>"+request.getParameter("piUid")+"</value>");
	toFile.println("	<type>int</type>");
	toFile.println("	<help>User ID of Principal Investigator</help>");
	toFile.println("</param>");
	toFile.println("");
	toFile.println("<param>");
	toFile.println("	<name>redundantHardware</name>");
	toFile.println("	<value>"+(request.getParameter("readoutHardware").equals("redundant") ? true : false)+"</value>");
	toFile.println("	<type>bool</type>");
	toFile.println("	<help>Flag to indicate whether or not to use the redundant right amplifier rather than left amplifier for CCD readout</help>");
	toFile.println("</param>");
	toFile.println("");
	toFile.println("<param>");
	toFile.println("	<name>strayLightThreshold</name>");
	toFile.println("	<value>"+request.getParameter("strayLightThreshold")+"</value>");
	toFile.println("	<type>double</type>");
	toFile.println("	<help>Stray light threshold for writing out images in photons/s/pixel, to apply when not defined in MPS_PRE_Visits</help>");
	toFile.println("</param>");

	if (request.getParameter("runStarProducer")!=null) {
		
	    toFile.println("");
	    toFile.println("<module>");
	    toFile.println("  <name>StarProducer</name>");
	    toFile.println("");
	    toFile.println("  	<param>");
	    toFile.println("        <name>FOVradius</name>");
	    toFile.println("        <value>"+fovRadius+"</value>");
	    toFile.println("	    <type>double</type>");
	    toFile.println("        <help>Field of view radius in arcseconds</help>");
	    toFile.println("    </param>");
	    toFile.println("    ");
	    toFile.println("    <param>");
	    toFile.println("        <name>minMagnitude</name>");
	    toFile.println("        <value>"+request.getParameter("minMagnitude")+"</value>");
	    toFile.println("	    <type>double</type>");
	    toFile.println("        <help>Minimum magnitude for stars in the field of view</help>");
	    toFile.println("    </param>");
	    toFile.println("    ");
	    toFile.println("    <param>");
	    toFile.println("        <name>targetMagnitude</name>");
	    toFile.println("        <value>"+request.getParameter("targetMagnitude")+"</value>");
	    toFile.println("        <help>Magnitude of target star in passband defined by gaiaBand parameter. If empty, the value is taken from the first row in starFilename, converting ot the appropriate passband as necessary.</help>");
	    toFile.println("    </param>");
	    toFile.println("    ");
	    toFile.println("    <param>");
	    toFile.println("        <name>starFilename</name>");
	    toFile.println("        <value>"+starFilename+"</value>");
	    toFile.println("        <help>ascii file containing comma separated values for RA,dec,mag,specType for each star to be included</help>");
	    toFile.println("    </param>");
	    toFile.println("    ");
	    toFile.println("    <param>");
	    toFile.println("        <name>gaiaBand</name>");
	    toFile.println("        <value>"+(request.getParameter("passband").equals("GaiaBand") ? true : false)+"</value>");
	    toFile.println("        <type>bool</type>");
	    toFile.println("        <help>Flag to indicate whether or not to use the Gaia band rather than the V-band for stellar flux normalization</help>");
	    toFile.println("    </param>");
	    toFile.println("    ");
	    toFile.println("    <param>");
	    toFile.println("        <name>useTargetStarForPointing</name>");
	    toFile.println("        <value>"+(request.getParameter("useTargetStarForPointing")!=null ? true : false)+"</value>");
	    toFile.println("	    <type>bool</type>");
	    toFile.println("        <help>Flag to indicate whether or not to match the pointing direction to the target star</help>");
	    toFile.println("    </param>");
	    toFile.println("    ");
	    toFile.println("    <param>");
	    toFile.println("        <name>doBackgroundStars</name>");
	    toFile.println("        <value>"+(request.getParameter("doBackgroundStars")!=null ? true : false)+"</value>");
	    toFile.println("	    <type>bool</type>");
	    toFile.println("        <help>Switch on to add a background star field</help>");
	    toFile.println("    </param>");
	    toFile.println("    ");
	    toFile.println("    <param>");
	    toFile.println("        <name>fieldCrowding</name>");
	    toFile.println("        <value>"+request.getParameter("fieldCrowding")+"</value>");
	    toFile.println("        <help>Background star field crowding (low, medium or extreme)</help>");
	    toFile.println("    </param>");
	    toFile.println("    ");
	    toFile.println("</module>");

	}

	for (int i=0; i<3; i++) {

	    String istar = Integer.toString(i);
		
	    if (request.getParameter("runTransitFluxModulator_star"+istar)!=null) {
		
		toFile.println("");
		toFile.println("<module>");
		toFile.println("  <name>TransitFluxModulator_star"+istar+"</name>");
		toFile.println("");
		toFile.println("    <param>");
		toFile.println("        <name>firstTransitTime</name>");
		toFile.println("        <value>"+request.getParameter("firstTransitTime_star"+istar+"")+"</value>");
		toFile.println("	    <type>double</type>");
		toFile.println("        <help>Time of midpoint of first transit as a fraction of the simulation duration</help>");
		toFile.println("    </param>");
		toFile.println("    ");
		toFile.println("  	<param>");
		toFile.println("        <name>planetRadius</name>");
		toFile.println("        <value>"+request.getParameter("planetRadius_star"+istar+"")+"</value>");
		toFile.println("	    <type>double</type>");
		toFile.println("        <help>Planet radius as a multiple of the radius of Jupiter/Neptune/Earth depending on planetScale</help>");
		toFile.println("    </param>");
		toFile.println("    ");
		toFile.println("  	<param>");
		toFile.println("        <name>planetScale</name>");
		toFile.println("        <value>"+request.getParameter("planetScale_star"+istar+"")+"</value>");
		toFile.println("        <help>Planet for which planetRadius is a multiple of</help>");
		toFile.println("    </param>");
		toFile.println("    ");
		toFile.println("    <param>");
		toFile.println("        <name>orbitPeriod</name>");
		toFile.println("        <value>"+request.getParameter("orbitPeriod_star"+istar+"")+"</value>");
		toFile.println("	    <type>double</type>");
		toFile.println("        <help>Planet orbit period around the star in hours/days depending on orbitUnits</help>");
		toFile.println("    </param>");
		toFile.println("    ");
		toFile.println("    <param>");
		toFile.println("        <name>orbitUnits</name>");
		toFile.println("        <value>"+request.getParameter("orbitUnits_star"+istar+"")+"</value>");
		toFile.println("        <help>hours or days</help>");
		toFile.println("    </param>");
		toFile.println("    ");
		toFile.println("    <param>");
		toFile.println("        <name>impactParameter</name>");
		toFile.println("        <value>"+request.getParameter("impactParameter_star"+istar+"")+"</value>");
		toFile.println("	    <type>double</type>");
		toFile.println("        <help>Impact parameter for the transit (smallest distance from planet centre to star centre divided by star radius)</help>");
		toFile.println("    </param>");
		toFile.println("    ");
		toFile.println("    <param>");
		toFile.println("        <name>doLimbDarkening</name>");
		toFile.println("        <value>"+(request.getParameter("doLimbDarkening_star"+istar+"")!=null ? true : false)+"</value>");
		toFile.println("	    <type>bool</type>");
		toFile.println("        <help>Set to true to include limb darkening</help>");
		toFile.println("    </param>");
		toFile.println("    ");
		toFile.println("    <param>");
		toFile.println("        <name>doModification</name>");
		toFile.println("        <value>false</value>");
		toFile.println("	    <type>bool</type>");
		toFile.println("        <help>Set to true to modify the transit curve according to resources/transitModification.txt</help>");
		toFile.println("    </param>");
		toFile.println("    ");
		toFile.println("</module>");

	    }
		
	}

	if (request.getParameter("runStellarVariationFluxModulator_star0")!=null) {

	    toFile.println("");
	    toFile.println("<module>");
	    toFile.println("  <name>StellarVariationFluxModulator_star0</name>");
	    toFile.println("");
	    toFile.println("    <param>");
	    toFile.println("        <name>rotationPeriod</name>");
	    toFile.println("        <value>"+request.getParameter("rotationPeriod")+"</value>");
	    toFile.println("	    <type>double</type>");
	    toFile.println("        <help>Rotation period of the star in days</help>");
	    toFile.println("    </param>");
	    toFile.println("    ");
	    toFile.println("    <param>");
	    toFile.println("        <name>stellarVariationSeed</name>");
	    toFile.println("        <value>"+request.getParameter("stellarVariationSeed")+"</value>");
	    toFile.println("	    <type>int</type>");
	    toFile.println("        <help>Seed for random number generation</help>");
	    toFile.println("    </param>");
	    toFile.println("    ");
	    toFile.println("</module>");

	}

	if (request.getParameter("runUserFluxModifier_star0")!=null) {

	    toFile.println("");
	    toFile.println("<module>");
	    toFile.println("  <name>UserFluxModifier_star0</name>");
	    toFile.println("");
	    toFile.println("    <param>");
	    toFile.println("        <name>userFluxFilename</name>");
	    toFile.println("        <value>"+userFluxFilename+"</value>");
	    toFile.println("        <help>Name of ascii file used to define the flux time series</help>");
	    toFile.println("    </param>");
	    toFile.println("    ");
	    toFile.println("</module>");

	}

	if (request.getParameter("runJitterProducer")!=null) {
		
	    toFile.println("");
	    toFile.println("<module>");
	    toFile.println("  <name>JitterProducer</name>");
	    toFile.println("");
	    toFile.println("    <param>");
	    toFile.println("        <name>jitterFilename</name>");
	    toFile.println("        <value>"+jitterFilename+"</value>");
	    toFile.println("        <help>filename for jitter, format: REF_APP_Jitter.fsd</help>");
	    toFile.println("    </param>");
	    toFile.println("    ");
	    toFile.println("    <param>");
	    toFile.println("        <name>jitterFileOffset</name>");
	    toFile.println("        <value>"+request.getParameter("jitterFileOffset")+"</value>");
	    toFile.println("	    <type>double</type>");
	    toFile.println("        <help>Number of seconds to omit from the start of the uploaded jitter file</help>");
	    toFile.println("    </param>");
	    toFile.println("    ");
	    toFile.println("    <param>");
	    toFile.println("        <name>jitterScale</name>");
	    toFile.println("        <value>"+request.getParameter("jitterScale")+"</value>");
	    toFile.println("	    <type>double</type>");
	    toFile.println("        <help>Scale factor applied to jitter RMS.  RMS for default jitter file is 2.96015</help>");
	    toFile.println("    </param>");
	    toFile.println("    ");
	    toFile.println("</module>");
	    
	}
	    
	if (request.getParameter("runOrbitSimulator")!=null) {

	    toFile.println("");
	    toFile.println("<module>");
	    toFile.println("  <name>OrbitSimulator</name>");
	    toFile.println("");
	    toFile.println("    <param>");
	    toFile.println("        <name>ccdTemperatureVariation</name>");
	    toFile.println("        <value>"+(request.getParameter("ccdTemperatureVariation")!=null ? true : false)+"</value>");
	    toFile.println("	    <type>bool</type>");
	    toFile.println("        <help>Set to true to apply CCD temperature variation</help>");
	    toFile.println("    </param>");
	    toFile.println("    ");
	    toFile.println("    <param>");
	    toFile.println("        <name>ccdMeanTemperature</name>");
	    toFile.println("        <value>"+request.getParameter("ccdMeanTemperature")+"</value>");
	    toFile.println("	    <type>double</type>");
	    toFile.println("        <help>CCD mean temperature in Kelvin</help>");
	    toFile.println("    </param>");
	    toFile.println("    ");
	    toFile.println("    <param>");
	    toFile.println("        <name>ccdTemperatureAmplitude</name>");
	    toFile.println("        <value>"+request.getParameter("ccdTemperatureAmplitude")+"</value>");
	    toFile.println("	    <type>double</type>");
	    toFile.println("        <help>Amplitude of sinusoidal variation of CCD temperature in Kelvin</help>");
	    toFile.println("    </param>");
	    toFile.println("    ");
	    toFile.println("    <param>");
	    toFile.println("        <name>ccdTemperaturePeriod</name>");
	    toFile.println("        <value>"+request.getParameter("ccdTemperaturePeriod")+"</value>");
	    toFile.println("	    <type>double</type>");
	    toFile.println("        <help>Period of sinusoidal variation of CCD temperature in minutes</help>");
	    toFile.println("    </param>");
	    toFile.println("    ");
	    toFile.println("    <param>");
	    toFile.println("        <name>feeTemperatureVariation</name>");
	    toFile.println("        <value>"+(request.getParameter("feeTemperatureVariation")!=null ? true : false)+"</value>");
	    toFile.println("	    <type>bool</type>");
	    toFile.println("        <help>Set to true to apply FEE temperature variation</help>");
	    toFile.println("    </param>");
	    toFile.println("    ");
	    toFile.println("    <param>");
	    toFile.println("        <name>feeMeanTemperature</name>");
	    toFile.println("        <value>"+request.getParameter("feeMeanTemperature")+"</value>");
	    toFile.println("	    <type>double</type>");
	    toFile.println("        <help>FEE mean temperature in Kelvin</help>");
	    toFile.println("    </param>");
	    toFile.println("    ");
	    toFile.println("    <param>");
	    toFile.println("        <name>feeTemperatureAmplitude</name>");
	    toFile.println("        <value>"+request.getParameter("feeTemperatureAmplitude")+"</value>");
	    toFile.println("	    <type>double</type>");
	    toFile.println("        <help>Amplitude of sinusoidal variation of FEE temperature in Kelvin</help>");
	    toFile.println("    </param>");
	    toFile.println("    ");
	    toFile.println("    <param>");
	    toFile.println("        <name>feeTemperaturePeriod</name>");
	    toFile.println("        <value>"+request.getParameter("feeTemperaturePeriod")+"</value>");
	    toFile.println("	    <type>double</type>");
	    toFile.println("        <help>Period of sinusoidal variation of FEE temperature in minutes</help>");
	    toFile.println("    </param>");
	    toFile.println("    ");
	    toFile.println("    <param>");
	    toFile.println("        <name>telescopeTemperatureVariation</name>");
	    toFile.println("        <value>"+(request.getParameter("telescopeTemperatureVariation")!=null ? true : false)+"</value>");
	    toFile.println("	    <type>bool</type>");
	    toFile.println("        <help>Set to true to apply telescope temperature variation</help>");
	    toFile.println("    </param>");
	    toFile.println("    ");
	    toFile.println("    <param>");
	    toFile.println("        <name>telescopeMeanTemperature</name>");
	    toFile.println("        <value>"+request.getParameter("telescopeMeanTemperature")+"</value>");
	    toFile.println("	    <type>double</type>");
	    toFile.println("        <help>telescope mean temperature in Kelvin</help>");
	    toFile.println("    </param>");
	    toFile.println("    ");
	    toFile.println("    <param>");
	    toFile.println("        <name>telescopeTemperatureAmplitude</name>");
	    toFile.println("        <value>"+request.getParameter("telescopeTemperatureAmplitude")+"</value>");
	    toFile.println("	    <type>double</type>");
	    toFile.println("        <help>Amplitude of sinusoidal variation of telescope temperature in Kelvin</help>");
	    toFile.println("    </param>");
	    toFile.println("    ");
	    toFile.println("    <param>");
	    toFile.println("        <name>telescopeTemperaturePeriod</name>");
	    toFile.println("        <value>"+request.getParameter("telescopeTemperaturePeriod")+"</value>");
	    toFile.println("	    <type>double</type>");
	    toFile.println("        <help>Period of sinusoidal variation of telescope temperature in minutes</help>");
	    toFile.println("    </param>");
	    toFile.println("    ");
	    toFile.println("    <param>");
	    toFile.println("        <name>telescopeTemperatureFromFile</name>");
	    toFile.println("        <value>"+(telescopeTemperatureFromFile || telescopeTemperatureFromUpload)+"</value>");
	    toFile.println("	    <type>bool</type>");
	    toFile.println("        <help>Set to true to read the telescope temperature vs time from a file</help>");
	    toFile.println("    </param>");
	    toFile.println("    ");
	    toFile.println("    <param>");
	    toFile.println("        <name>telescopeTemperatureFilename</name>");
	    toFile.println("        <value>"+telescopeTemperatureFilename+"</value>");
	    toFile.println("        <help>Filename for telescope temperature vs time, format: REF_APP_Temperature.fsd</help>");
	    toFile.println("    </param>");
	    toFile.println("    ");
	    toFile.println("    <param>");
	    toFile.println("        <name>rotateFOV</name>");
	    toFile.println("        <value>"+(request.getParameter("rotateFOV")!=null ? true : false)+"</value>");
	    toFile.println("	    <type>bool</type>");
	    toFile.println("        <help>Set to true to uniformly rotate the FOV once for each orbit period</help>");
	    toFile.println("    </param>");
	    toFile.println("    ");
	    toFile.println("    <param>");
	    toFile.println("        <name>orbitFilename</name>");
	    toFile.println("        <value>"+orbitFilename+"</value>");
	    toFile.println("        <help>Filename for orbit position vs time, format: name of directory containing AUX_REF_Orbit files, or name of AUX_RES_Orbit file</help>");
	    toFile.println("    </param>");
	    toFile.println("    ");
	    toFile.println("    <param>");
	    toFile.println("        <name>minAngleToOrbitalPlane</name>");
	    toFile.println("        <value>"+request.getParameter("minAngleToOrbitalPlane")+"</value>");
	    toFile.println("	    <type>double</type>");
	    toFile.println("        <help>Minimum angle (degrees) between pointing direction and orbital plane for roll angle calculation</help>");
	    toFile.println("    </param>");
	    toFile.println("    ");
	    toFile.println("    <param>");
	    toFile.println("        <name>attitudeCadence</name>");
	    toFile.println("        <value>"+request.getParameter("attitudeCadence")+"</value>");
	    toFile.println("	    <type>int</type>");
	    toFile.println("        <help>Cadence in seconds for writing out spacecraft attitude data to SCI_RAW_Attitude data structure</help>");
	    toFile.println("    </param>");
	    toFile.println("    ");
	    toFile.println("    <param>");
	    toFile.println("        <name>orbitCadence</name>");
	    toFile.println("        <value>"+request.getParameter("orbitCadence")+"</value>");
	    toFile.println("	    <type>int</type>");
	    toFile.println("        <help>Cadence in minutes for writing out spacecraft orbit data to AUX_RES_Orbit data structure</help>");
	    toFile.println("    </param>");
	    toFile.println("    ");
	    toFile.println("    <param>");
	    toFile.println("        <name>SAAMapFilename</name>");
	    toFile.println("        <value>"+request.getParameter("SAAMapFilename")+"</value>");
	    toFile.println("        <help>Filename for FITS file containing the SAA map</help>");
	    toFile.println("    </param>");
	    toFile.println("    ");
	    toFile.println("    <param>");
	    toFile.println("        <name>saaFlagFromVisitConstraints</name>");
	    toFile.println("        <value>"+(request.getParameter("saaFlagFromVisitConstraints")!=null ? true : false)+"</value>");
	    toFile.println("	    <type>bool</type>");
	    toFile.println("        <help>Flag to indicate whether to read the SAA flags, stray light flags and Earth occultation flags from MPS_PRE_VisitConstraints rather than calculating from the orbit file</help>");
	    toFile.println("    </param>");
	    toFile.println("    ");
	    toFile.println("</module>");

	}
	    
	if (request.getParameter("runFocalPlaneGenerator")!=null) {

	    toFile.println("");
	    toFile.println("<module>");
	    toFile.println("  <name>FocalPlaneGenerator</name>");
	    toFile.println("");
	    toFile.println("    <param>");
	    toFile.println("        <name>subArrayXDim</name>");
	    toFile.println("        <value>"+(request.getParameter("imageSizeRadio").equals("subArray") ? request.getParameter("subArrayXDim") : 1024)+"</value>");
	    toFile.println("	    <type>int</type>");
	    toFile.println("        <help>Number of pixels in X dimension of image sub-array</help>");
	    toFile.println("    </param>");
	    toFile.println("    ");
	    toFile.println("    <param>");
	    toFile.println("        <name>subArrayYDim</name>");
	    toFile.println("        <value>"+(request.getParameter("imageSizeRadio").equals("subArray") ? request.getParameter("subArrayYDim") : 1024)+"</value>");
	    toFile.println("	    <type>int</type>");
	    toFile.println("        <help>Number of pixels in Y dimension of image sub-array</help>");
	    toFile.println("    </param>");
	    toFile.println("    ");
	    toFile.println("    <param>");
	    toFile.println("        <name>subArrayXOffset</name>");
	    toFile.println("        <value>"+(request.getParameter("imageSizeRadio").equals("subArray") ? request.getParameter("subArrayXOffset") : 0)+"</value>");
	    toFile.println("	    <type>int</type>");
	    toFile.println("        <help>Offset of first pixel of image sub-array in X dimension w.r.t. CCD edge</help>");
	    toFile.println("    </param>");
	    toFile.println("    ");
	    toFile.println("    <param>");
	    toFile.println("        <name>subArrayYOffset</name>");
	    toFile.println("        <value>"+(request.getParameter("imageSizeRadio").equals("subArray") ? request.getParameter("subArrayYOffset") : 0)+"</value>");
	    toFile.println("	    <type>int</type>");
	    toFile.println("        <help>Offset of first pixel of image sub-array in Y dimension w.r.t. CCD edge</help>");
	    toFile.println("    </param>");
	    toFile.println("    ");
	    toFile.println("    <param>");
	    toFile.println("        <name>targetLocationX</name>");
	    toFile.println("        <value>"+request.getParameter("targetLocationX")+"</value>");
	    toFile.println("	    <type>double</type>");
	    toFile.println("        <help>Location of centre of FOV rotation in X dimension w.r.t. CCD edge</help>");
	    toFile.println("    </param>");
	    toFile.println("    ");
	    toFile.println("    <param>");
	    toFile.println("        <name>targetLocationY</name>");
	    toFile.println("        <value>"+request.getParameter("targetLocationY")+"</value>");
	    toFile.println("	    <type>double</type>");
	    toFile.println("        <help>Location of centre of FOV rotation in Y dimension w.r.t. CCD edge</help>");
	    toFile.println("    </param>");
	    toFile.println("    ");
	    toFile.println("    <param>");
	    toFile.println("        <name>targetLocationListX</name>");
	    toFile.println("        <value>"+request.getParameter("targetLocationListX")+"</value>");
	    toFile.println("        <help>Comma separated list of target location x coordinates, with one entry per exposure, intended for MandC data</help>");
	    toFile.println("    </param>");
	    toFile.println("    ");
	    toFile.println("    <param>");
	    toFile.println("        <name>targetLocationListY</name>");
	    toFile.println("        <value>"+request.getParameter("targetLocationListY")+"</value>");
	    toFile.println("        <help>Comma separated list of target location y coordinates, with one entry per exposure, intended for MandC data</help>");
	    toFile.println("    </param>");
	    toFile.println("    ");
	    toFile.println("</module>");

	}
	    
	if (request.getParameter("runStrayLightGenerator")!=null) {

	    toFile.println("");
	    toFile.println("<module>");
	    toFile.println("  <name>StrayLightGenerator</name>");
	    toFile.println("");
	    toFile.println("    <param>");
	    toFile.println("        <name>strayLightFile</name>");
	    toFile.println("        <value>"+strayLightFilename+"</value>");
	    toFile.println("        <help>Filename for stray light</help>");
	    toFile.println("    </param>");
	    toFile.println("    ");
	    toFile.println("</module>");

	}
	    
	if (request.getParameter("runHaloGenerator")!=null) {
		
	    toFile.println("");
	    toFile.println("<module>");
	    toFile.println("  <name>HaloGenerator</name>");
	    toFile.println("");
	    toFile.println("    <param>");
	    toFile.println("        <name>doGhosts</name>");
	    toFile.println("        <value>"+(request.getParameter("doGhosts")!=null ? true : false)+"</value>");
	    toFile.println("	    <type>bool</type>");
	    toFile.println("        <help>Flag to indicate whether or not to include flux due to ghosts</help>");
	    toFile.println("    </param>");
	    toFile.println("    ");
	    toFile.println("</module>");

	}
	    
	if (request.getParameter("runPSFGenerator")!=null) {
	    
	    toFile.println("");
	    toFile.println("<module>");
	    toFile.println("  <name>PSFGenerator</name>");
	    toFile.println("");
	    toFile.println("    <param>");
	    toFile.println("        <name>PSF_filename</name>");
	    toFile.println("        <value>"+psfFilename+"</value>");
	    toFile.println("        <help>Filename for fits file used to define the PSF, format: REF_APP_(Oversampled)(White|Coloured)PSF.fsd with meatadata REF_APP_(Oversampled)(White|Coloured)PSFMetadata.fsd</help>");
	    toFile.println("    </param>");
	    toFile.println("    ");
	    toFile.println("    <param>");
	    toFile.println("        <name>monochromaticPSF</name>");
	    toFile.println("        <value>"+(request.getParameter("PsfType").equals("monochromatic") ? true : false)+"</value>");
	    toFile.println("	    <type>bool</type>");
	    toFile.println("        <help>Set to true to use a monochromatic (wavelength binned) PSF</help>");
	    toFile.println("    </param>");
	    toFile.println("    ");
	    toFile.println("    <param>");
	    toFile.println("        <name>monochromaticPSFwavelength</name>");
	    toFile.println("        <value>"+((request.getParameter("PsfType").equals("monochromatic") && request.getParameter("monoPSFweighting").equals("singleWavelength")) ? request.getParameter("monoPSFwavelength") : 0)+"</value>");
	    toFile.println("	    <type>int</type>");
	    toFile.println("        <help>Wavelength for monochromatic PSF. Set to 0 for combination over all wavelengths.</help>");
	    toFile.println("    </param>");
	    toFile.println("    ");
	    toFile.println("    <param>");
	    toFile.println("        <name>thermalMap</name>");
	    toFile.println("        <value>"+request.getParameter("thermalMap")+"</value>");
	    toFile.println("        <help>Specify the termal map for the PSF: fixed, cold, hot1, hot2 or breathing</help>");
	    toFile.println("    </param>");
	    toFile.println("    ");
	    toFile.println("    <param>");
	    toFile.println("        <name>oversampleJitter</name>");
	    toFile.println("        <value>"+request.getParameter("oversampleJitter")+"</value>");
	    toFile.println("	    <type>bool</type>");
	    toFile.println("        <help>Set to true to perform time oversampling of PSF position according to jitter and field of view rotation</help>");
	    toFile.println("    </param>");
	    toFile.println("    ");
	    toFile.println("    <param>");
	    toFile.println("        <name>targetStarPositioning</name>");
	    toFile.println("        <value>"+request.getParameter("targetStarPositioning")+"</value>");
	    toFile.println("        <help>Method for positioning the target star PSF onto the pixel grid: Oversampling, Interpolation, or SnapToGrid</help>");
	    toFile.println("    </param>");
	    toFile.println("    ");
	    toFile.println("    <param>");
	    toFile.println("        <name>backgroundStarPositioning</name>");
	    toFile.println("        <value>"+request.getParameter("backgroundStarPositioning")+"</value>");
	    toFile.println("        <help>Method for positioning background star PSFs onto the pixel grid: Oversampling, Interpolation, or SnapToGrid</help>");
	    toFile.println("    </param>");
	    toFile.println("    ");
	    toFile.println("    <param>");
	    toFile.println("        <name>convertFluxToPhotons</name>");
	    toFile.println("        <value>true</value>");
	    toFile.println("	    <type>bool</type>");
	    toFile.println("        <help>Switch off to perform a closure test that the input flux is recovered in the extracted light curve</help>");
	    toFile.println("    </param>");
	    toFile.println("    ");
	    toFile.println("</module>");

	}
	
	if (request.getParameter("runFlatFieldGenerator")!=null) {
		
	    toFile.println("");
	    toFile.println("<module>");
	    toFile.println("  <name>FlatFieldGenerator</name>");
	    toFile.println("");
	    toFile.println("    <param>");
	    toFile.println("        <name>applyFlatField</name>");
	    toFile.println("        <value>"+(request.getParameter("applyFlatField")!=null ? true : false)+"</value>");
	    toFile.println("	    <type>bool</type>");
	    toFile.println("        <help>Set to true to apply a flat field</help>");
	    toFile.println("    </param>");
	    toFile.println("    ");
	    toFile.println("    <param>");
	    toFile.println("        <name>gaussianFlatField</name>");
	    toFile.println("        <value>"+(request.getParameter("flatFieldMethod").equals("gaussian") ? true : false)+"</value>");
	    toFile.println("	    <type>bool</type>");
	    toFile.println("        <help>Set to false to read empirical flat fields. Set to true to define flat fields according to a Gaussian.</help>");
	    toFile.println("    </param>");
	    toFile.println("    ");
	    toFile.println("    <param>");
	    toFile.println("        <name>flatFieldSeed</name>");
	    toFile.println("        <value>"+request.getParameter("flatFieldSeed")+"</value>");
	    toFile.println("	    <type>int</type>");
	    toFile.println("        <help>Random number generator seed for Gaussian flat field</help>");
	    toFile.println("    </param>");
	    toFile.println("    ");
	    toFile.println("    <param>");
	    toFile.println("        <name>flatFieldFilename</name>");
	    toFile.println("        <value>"+flatFieldFilename+"</value>");
	    toFile.println("        <help>filename for fits file used to define the flat field, format: REF_APP_FlatFieldTeff.fsd with meatadata REF_APP_FlatFieldTeffMetadata.fsd</help>");
	    toFile.println("    </param>");
	    toFile.println("    ");
	    toFile.println("    <param>");
	    toFile.println("        <name>flatFieldToSubtractFilename</name>");
	    toFile.println("	    <value>"+request.getParameter("flatFieldToSubtractFilename")+"</value>");
	    toFile.println("        <help>filename for fits file used to define the flat field to optionally be used for the flat field correction in the CHEOPSim DataReduction module, format: REF_APP_FlatFieldTeff.fsd with meatadata REF_APP_FlatFieldTeffMetadata.fsd</help>");
	    toFile.println("    </param>");
	    toFile.println("    ");
	    toFile.println("    <param>");
	    toFile.println("        <name>flatFieldScaleFactor</name>");
	    toFile.println("        <value>"+request.getParameter("flatFieldScaleFactor")+"</value>");
	    toFile.println("	    <type>double</type>");
	    toFile.println("        <help>Scale factor to apply to deviations from the mean for empirical flat field</help>");
	    toFile.println("    </param>");
	    toFile.println("    ");
	    toFile.println("    <param>");
	    toFile.println("        <name>flatFieldSigma</name>");
	    toFile.println("        <value>"+request.getParameter("flatFieldSigma")+"</value>");
	    toFile.println("	    <type>double</type>");
	    toFile.println("        <help>Standard deviation of the Gaussian distribution used to simulate the flat field</help>");
	    toFile.println("    </param>");
	    toFile.println("    ");
	    toFile.println("    <param>");
	    toFile.println("        <name>flatFieldTeffOffset</name>");
	    toFile.println("        <value>"+request.getParameter("flatFieldTeffOffset")+"</value>");
	    toFile.println("	    <type>double</type>");
	    toFile.println("        <help>Offset to the black body effective temperature to define the spectral distribution of the flat field in order that the flat field applied in CHEOPSim is not identical to that used for flat field correction in data reduction</help>");
	    toFile.println("    </param>");
	    toFile.println("    ");
	    toFile.println("    <param>");
	    toFile.println("        <name>flatFieldSmearSigma</name>");
	    toFile.println("        <value>"+request.getParameter("flatFieldSmearSigma")+"</value>");
	    toFile.println("	    <type>double</type>");
	    toFile.println("        <help>Standard deviation of the Gaussian distribution used to smear the flat field in order that the flat field applied in CHEOPSim is not identical to that used for flat field correction in data reduction</help>");
	    toFile.println("    </param>");
	    toFile.println("    ");
	    toFile.println("    <param>");
	    toFile.println("        <name>doDeadPixels</name>");
	    toFile.println("        <value>"+(request.getParameter("doDeadPixels")!=null ? true : false)+"</value>");
	    toFile.println("	    <type>bool</type>");
	    toFile.println("        <help>Simulate dead pixels</help>");
	    toFile.println("    </param>");
	    toFile.println("    ");
	    toFile.println("    <param>");
	    toFile.println("        <name>deadPixelPositionSeed</name>");
	    toFile.println("        <value>"+request.getParameter("deadPixelPositionSeed")+"</value>");
	    toFile.println("	    <type>int</type>");
	    toFile.println("        <help>Seed for random number generation for dead pixel positions</help>");
	    toFile.println("    </param>");
	    toFile.println("    ");
	    toFile.println("    <param>");
	    toFile.println("        <name>fracDeadPixels</name>");
	    toFile.println("        <value>"+request.getParameter("fracDeadPixels")+"</value>");
	    toFile.println("	    <type>double</type>");
	    toFile.println("        <help>Fraction of pixels which are dead</help>");
	    toFile.println("    </param>");
	    toFile.println("    ");
	    toFile.println("    <param>");
	    toFile.println("        <name>deadPixelRelativeQE</name>");
	    toFile.println("        <value>"+request.getParameter("deadPixelRelativeQE")+"</value>");
	    toFile.println("	    <type>double</type>");
	    toFile.println("        <help>Factor by which quantum efficiency is reduced for dead pixels</help>");
	    toFile.println("    </param>");
	    toFile.println("    ");
	    toFile.println("    <param>");
	    toFile.println("        <name>writeTruthFlatField</name>");
	    toFile.println("        <value>"+(request.getParameter("writeTruthFlatField")!=null ? true : false)+"</value>");
	    toFile.println("	    <type>bool</type>");
	    toFile.println("        <help>Set to true to output a fits file containing the truth flat field</help>");
	    toFile.println("    </param>");
	    toFile.println("    ");
	    toFile.println("</module>");

	}
	    
	if (request.getParameter("runDarkCurrentGenerator")!=null) {
		
	    toFile.println("");
	    toFile.println("<module>");
	    toFile.println("  <name>DarkCurrentGenerator</name>");
	    toFile.println("");
	    toFile.println("    <param>");
	    toFile.println("        <name>empiricalDarkFrame</name>");
	    toFile.println("        <value>"+(request.getParameter("darkCurrentMethod").equals("file") ? true : false)+"</value>");
	    toFile.println("	    <type>bool</type>");
	    toFile.println("        <help>Set to true to define the expected dark current in each pixel based on an empirical dark frame rather than a fixed value for all pixels</help>");
	    toFile.println("    </param>");
	    toFile.println("    ");
	    toFile.println("    <param>");
	    toFile.println("        <name>darkFrameFilename</name>");
	    toFile.println("        <value>"+request.getParameter("darkFrameFilename")+"</value>");
	    toFile.println("        <help>Filename for fits file used to define the empirical dark frame, format: REF_APP_DarkFrame.fsd</help>");
	    toFile.println("    </param>");
	    toFile.println("    ");
	    toFile.println("    <param>");
	    toFile.println("        <name>darkFrameScaleFactor</name>");
	    toFile.println("        <value>"+request.getParameter("darkFrameScaleFactor")+"</value>");
	    toFile.println("	    <type>double</type>");
	    toFile.println("        <help>Scale factor to apply to the empirical dark frame</help>");
	    toFile.println("    </param>");
	    toFile.println("    ");
	    toFile.println("    <param>");
	    toFile.println("        <name>meanDarkCurrent233K</name>");
	    toFile.println("        <value>"+request.getParameter("meanDarkCurrent233K")+"</value>");
	    toFile.println("	    <type>double</type>");
	    toFile.println("        <help>Mean dark current at 233K in electrons/second/pixel</help>");
	    toFile.println("    </param>");
	    toFile.println("    ");
	    toFile.println("    <param>");
	    toFile.println("        <name>includeReadoutTime</name>");
	    toFile.println("        <value>"+(request.getParameter("includeReadoutTime")!=null ? true : false)+"</value>");
	    toFile.println("	    <type>bool</type>");
	    toFile.println("        <help>Flag to indicate whether or not to include readout time as well as exposure time for dark current accumulation</help>");
	    toFile.println("    </param>");
	    toFile.println("    ");
	    toFile.println("    <param>");
	    toFile.println("        <name>rowDownShiftTime</name>");
	    toFile.println("        <value>"+request.getParameter("rowDownShiftTime")+"</value>");
	    toFile.println("	    <type>double</type>");
	    toFile.println("        <help>Time to shift down one row during readount in microseconds</help>");
	    toFile.println("    </param>");
	    toFile.println("    ");
	    toFile.println("    <param>");
	    toFile.println("        <name>serialReadRate</name>");
	    toFile.println("        <value>"+request.getParameter("serialReadRate")+"</value>");
	    toFile.println("	    <type>double</type>");
	    toFile.println("        <help>Serial read rate in kHz</help>");
	    toFile.println("    </param>");
	    toFile.println("    ");
	    toFile.println("    <param>");
	    toFile.println("        <name>doHotPixels</name>");
	    toFile.println("        <value>"+(request.getParameter("doHotPixels")!=null ? true : false)+"</value>");
	    toFile.println("	    <type>bool</type>");
	    toFile.println("        <help>Randomly generate hot pixels</help>");
	    toFile.println("    </param>");
	    toFile.println("    ");
	    toFile.println("    <param>");
	    toFile.println("        <name>hotPixelPositionSeed</name>");
	    toFile.println("        <value>"+request.getParameter("hotPixelPositionSeed")+"</value>");
	    toFile.println("	    <type>int</type>");
	    toFile.println("        <help>Seed for random number generation for hot, warm and telegraphic pixel positions</help>");
	    toFile.println("    </param>");
	    toFile.println("    ");
	    toFile.println("    <param>");
	    toFile.println("        <name>fracHotPixels</name>");
	    toFile.println("        <value>"+request.getParameter("fracHotPixels")+"</value>");
	    toFile.println("	    <type>double</type>");
	    toFile.println("        <help>Fraction of pixels which are hot</help>");
	    toFile.println("    </param>");
	    toFile.println("    ");
	    toFile.println("    <param>");
	    toFile.println("        <name>hotPixelRelativeDarkCurrent</name>");
	    toFile.println("        <value>"+request.getParameter("hotPixelRelativeDarkCurrent")+"</value>");
	    toFile.println("	    <type>double</type>");
	    toFile.println("        <help>Factor by which dark current is increased for hot pixels</help>");
	    toFile.println("    </param>");
	    toFile.println("    ");
	    toFile.println("    <param>");
	    toFile.println("        <name>doWarmPixels</name>");
	    toFile.println("        <value>"+(request.getParameter("doWarmPixels")!=null ? true : false)+"</value>");
	    toFile.println("	    <type>bool</type>");
	    toFile.println("        <help>Randomly generate warm pixels</help>");
	    toFile.println("    </param>");
	    toFile.println("    ");
	    toFile.println("    <param>");
	    toFile.println("        <name>fracWarmPixels</name>");
	    toFile.println("        <value>"+request.getParameter("fracWarmPixels")+"</value>");
	    toFile.println("	    <type>double</type>");
	    toFile.println("        <help>Fraction of pixels which are warm</help>");
	    toFile.println("    </param>");
	    toFile.println("    ");
	    toFile.println("    <param>");
	    toFile.println("        <name>warmPixelRelativeDarkCurrent</name>");
	    toFile.println("        <value>"+request.getParameter("warmPixelRelativeDarkCurrent")+"</value>");
	    toFile.println("	    <type>double</type>");
	    toFile.println("        <help>Factor by which dark current is increased for warm pixels</help>");
	    toFile.println("    </param>");
	    toFile.println("    ");
	    toFile.println("    <param>");
	    toFile.println("        <name>doTelegraphicPixels</name>");
	    toFile.println("        <value>"+(request.getParameter("doTelegraphicPixels")!=null ? true : false)+"</value>");
	    toFile.println("	    <type>bool</type>");
	    toFile.println("        <help>Randomly generate telegraphic pixels</help>");
	    toFile.println("    </param>");
	    toFile.println("    ");
	    toFile.println("    <param>");
	    toFile.println("        <name>fracTelegraphicPixels</name>");
	    toFile.println("        <value>"+request.getParameter("fracTelegraphicPixels")+"</value>");
	    toFile.println("	    <type>double</type>");
	    toFile.println("        <help>Fraction of pixels which are telegraphic for the case of random position generation</help>");
	    toFile.println("    </param>");
	    toFile.println("    ");
	    toFile.println("    <param>");
	    toFile.println("        <name>telegraphicPixelRelativeDarkCurrent</name>");
	    toFile.println("        <value>"+request.getParameter("telegraphicPixelRelativeDarkCurrent")+"</value>");
	    toFile.println("	    <type>double</type>");
	    toFile.println("        <help>Factor by which dark current is increased for telegraphic pixels when in their active state</help>");
	    toFile.println("    </param>");
	    toFile.println("    ");
	    toFile.println("    <param>");
	    toFile.println("        <name>telegraphicTimeConstant</name>");
	    toFile.println("        <value>"+request.getParameter("telegraphicTimeConstant")+"</value>");
	    toFile.println("	    <type>double</type>");
	    toFile.println("        <help>Time constant for telegraphic pixel transitions in seconds</help>");
	    toFile.println("    </param>");
	    toFile.println("    ");
	    toFile.println("    <param>");
	    toFile.println("        <name>telegraphicTransitionSeed</name>");
	    toFile.println("        <value>"+request.getParameter("telegraphicTransitionSeed")+"</value>");
	    toFile.println("	    <type>int</type>");
	    toFile.println("        <help>Seed for random number generation for telegraphic pixel transitions</help>");
	    toFile.println("    </param>");
	    toFile.println("    ");
	    toFile.println("    <param>");
	    toFile.println("        <name>doManualHotPixels</name>");
	    toFile.println("        <value>"+(request.getParameter("doManualHotPixels")!=null ? true : false)+"</value>");
	    toFile.println("	    <type>bool</type>");
	    toFile.println("        <help>Set to true to define hot/warm/telegraphic pixels manually</help>");
	    toFile.println("    </param>");
	    toFile.println("    ");
	    toFile.println("    <param>");
	    toFile.println("        <name>manualHotPixelX</name>");
	    toFile.println("        <value>"+manualHotPixelX+"</value>");
	    toFile.println("        <help>Comma separated list of manually defined hot/warm/telegraphic pixel x positions</help>");
	    toFile.println("    </param>");
	    toFile.println("    ");
	    toFile.println("    <param>");
	    toFile.println("        <name>manualHotPixelY</name>");
	    toFile.println("        <value>"+manualHotPixelY+"</value>");
	    toFile.println("        <help>Comma separated list of manually defined hot/warm/telegraphic pixel y positions</help>");
	    toFile.println("    </param>");
	    toFile.println("    ");
	    toFile.println("    <param>");
	    toFile.println("        <name>manualHotPixelRate</name>");
	    toFile.println("        <value>"+manualHotPixelRate+"</value>");
	    toFile.println("        <help>Comma separated list of dark current rates at 233K for manually defined hot/warm/telegraphic pixels, in electrons/s/pixel</help>");
	    toFile.println("    </param>");
	    toFile.println("    ");
	    toFile.println("    <param>");
	    toFile.println("        <name>manualHotPixelIsTelegraphic</name>");
	    toFile.println("        <value>"+manualHotPixelIsTelegraphic+"</value>");
	    toFile.println("        <help>Comma separated list of flags for manually defined hot/warm/telegraphic pixels, to indicate if they are telegraphic (T) or not (F)</help>");
	    toFile.println("    </param>");
	    toFile.println("    ");
	    toFile.println("</module>");

	}
	
	if (request.getParameter("runFrameTransferSmearer")!=null) {
	    
	    toFile.println("");
	    toFile.println("<module>");
	    toFile.println("  <name>FrameTransferSmearer</name>");
	    toFile.println("");
	    toFile.println("    <param>");
	    toFile.println("        <name>transferClockPeriod</name>");
	    toFile.println("        <value>"+request.getParameter("transferClockPeriod")+"</value>");
	    toFile.println("	    <type>double</type>");
	    toFile.println("        <help>Frame transfer clock period in microseconds</help>");
	    toFile.println("    </param>");
	    toFile.println("    ");
	    toFile.println("</module>");

	}

	if (request.getParameter("runPhotonNoiseGenerator")!=null) {
		
	    toFile.println("");
	    toFile.println("<module>");
	    toFile.println("  <name>PhotonNoiseGenerator</name>");
	    toFile.println("");
	    toFile.println("    <param>");
	    toFile.println("        <name>photonNoiseSeed</name>");
	    toFile.println("        <value>"+request.getParameter("photonNoiseSeed")+"</value>");
	    toFile.println("	    <type>int</type>");
	    toFile.println("        <help>Random number generator seed for Poisson photon noise</help>");
	    toFile.println("    </param>");
	    toFile.println("    ");
	    toFile.println("</module>");

	}
	    
	if (request.getParameter("runCosmicRayGenerator")!=null) {
		
	    toFile.println("");
	    toFile.println("<module>");
	    toFile.println("  <name>CosmicRayGenerator</name>");
	    toFile.println("");
	    toFile.println("    <param>");
	    toFile.println("        <name>meanCosmicsPerMinute</name>");
	    toFile.println("        <value>"+request.getParameter("meanCosmicsPerMinute")+"</value>");
	    toFile.println("	    <type>double</type>");
	    toFile.println("        <help>Mean number of cosmic rays per minute on 200x200 pixels</help>");
	    toFile.println("    </param>");
	    toFile.println("    ");
	    toFile.println("    <param>");
	    toFile.println("        <name>SAAFluxFactor</name>");
	    toFile.println("        <value>"+request.getParameter("SAAFluxFactor")+"</value>");
	    toFile.println("	    <type>double</type>");
	    toFile.println("        <help>Factor by which the cosmic ray flux rate increases within the SAA</help>");
	    toFile.println("    </param>");
	    toFile.println("    ");
	    toFile.println("    <param>");
	    toFile.println("        <name>cosmicEnergyScaleFactor</name>");
	    toFile.println("        <value>"+request.getParameter("cosmicEnergyScaleFactor")+"</value>");
	    toFile.println("	    <type>double</type>");
	    toFile.println("        <help>Scale factor for cosmic ray energy distribution</help>");
	    toFile.println("    </param>");
	    toFile.println("    ");
	    toFile.println("    <param>");
	    toFile.println("        <name>cosmicSeed</name>");
	    toFile.println("        <value>"+request.getParameter("cosmicSeed")+"</value>");
	    toFile.println("	    <type>int</type>");
	    toFile.println("        <help>Seed for random number generation</help>");
	    toFile.println("    </param>");
	    toFile.println("    ");
	    toFile.println("</module>");

	}
	    
	if (request.getParameter("runFullWellSimulator")!=null) {

	    toFile.println("");
	    toFile.println("<module>");
	    toFile.println("  <name>FullWellSimulator</name>");
	    toFile.println("");
	    toFile.println("    <param>");
	    toFile.println("        <name>fullWellCapacity</name>");
	    toFile.println("        <value>"+request.getParameter("fullWellCapacity")+"</value>");
	    toFile.println("	    <type>int</type>");
	    toFile.println("        <help>Maximum number of electrons that can be held in a pixel</help>");
	    toFile.println("    </param>");
	    toFile.println("    ");
	    toFile.println("</module>");

	}
	    
	if (request.getParameter("runChargeTransferSimulator")!=null) {

	    toFile.println("");
	    toFile.println("<module>");
	    toFile.println("  <name>ChargeTransferSimulator</name>");
	    toFile.println("");
	    toFile.println("    <param>");
	    toFile.println("        <name>endOfLife</name>");
	    toFile.println("        <value>"+(request.getParameter("stageOfLife").equals("end") ? true : false)+"</value>");
	    toFile.println("	    <type>bool</type>");
	    toFile.println("        <help>Boolean to indicate whether or not to model end of life trails</help>");
	    toFile.println("    </param>");
	    toFile.println("    ");
	    toFile.println("    <param>");
	    toFile.println("        <name>cte_vertical</name>");
	    toFile.println("        <value>"+request.getParameter("cte_vertical")+"</value>");
	    toFile.println("	    <type>double</type>");
	    toFile.println("        <help>Vertical charge transfer efficiency (%)</help>");
	    toFile.println("    </param>");
	    toFile.println("    ");
	    toFile.println("    <param>");
	    toFile.println("        <name>cte_horizontal</name>");
	    toFile.println("        <value>"+request.getParameter("cte_horizontal")+"</value>");
	    toFile.println("	    <type>double</type>");
	    toFile.println("        <help>Horizontal charge transfer efficiency (%)</help>");
	    toFile.println("    </param>");
	    toFile.println("    ");
	    toFile.println("    <param>");
	    toFile.println("        <name>cti_trailLength</name>");
	    toFile.println("        <value>"+request.getParameter("cti_trailLength")+"</value>");
	    toFile.println("	    <type>double</type>");
	    toFile.println("        <help>Exponential decay constant for CTI trails</help>");
	    toFile.println("    </param>");
	    toFile.println("    ");
	    toFile.println("    <param>");
	    toFile.println("        <name>cti_trailFraction</name>");
	    toFile.println("        <value>"+request.getParameter("cti_trailFraction")+"</value>");
	    toFile.println("	    <type>double</type>");
	    toFile.println("        <help>Fraction of intensity transferred to CTI tails for an intensity of 10000 electrons</help>");
	    toFile.println("    </param>");
	    toFile.println("    ");
	    toFile.println("    <param>");
	    toFile.println("        <name>cti_intensityScaling</name>");
	    toFile.println("        <value>"+request.getParameter("cti_intensityScaling")+"</value>");
	    toFile.println("	    <type>double</type>");
	    toFile.println("        <help>Scaling exponent for fraction of intensity transferred to CTI tails as a function of intensity</help>");
	    toFile.println("    </param>");
	    toFile.println("    ");
	    toFile.println("</module>");

	}
	    
	if (request.getParameter("runBiasGenerator")!=null) {

	    toFile.println("");
	    toFile.println("<module>");
	    toFile.println("  <name>BiasGenerator</name>");
	    toFile.println("");
	    toFile.println("    <param>");
	    toFile.println("        <name>empiricalBiasFrame</name>");
	    toFile.println("        <value>"+(request.getParameter("biasMethod").equals("file") ? true : false)+"</value>");
	    toFile.println("	    <type>bool</type>");
	    toFile.println("        <help>Set to true to define the mean bias offset in each pixel based on an empirical bias frame rather than a uniform value for all pixels</help>");
	    toFile.println("    </param>");
	    toFile.println("    ");
	    toFile.println("    <param>");
	    toFile.println("        <name>biasFrameFilename</name>");
	    toFile.println("        <value>"+request.getParameter("biasFrameFilename")+"</value>");
	    toFile.println("        <help>Filename for fits file used to define the empirical bias frame, format: REF_APP_BiasFrame.fsd</help>");
	    toFile.println("    </param>");
	    toFile.println("    ");
	    toFile.println("    <param>");
	    toFile.println("        <name>applyAnalogElectronicsStability</name>");
	    toFile.println("        <value>"+(request.getParameter("applyAnalogElectronicsStability")!=null ? true : false)+"</value>");
	    toFile.println("	    <type>bool</type>");
	    toFile.println("        <help>Boolean to indicate whether or not to include analog electronics stability</help>");
	    toFile.println("    </param>");
	    toFile.println("    ");
	    toFile.println("    <param>");
	    toFile.println("        <name>doCcdNonLinearity</name>");
	    toFile.println("        <value>"+(request.getParameter("doCcdNonLinearity")!=null ? true : false)+"</value>");
	    toFile.println("	    <type>bool</type>");
	    toFile.println("        <help>Boolean to indicate whether or not to include CCD non-linearity</help>");
	    toFile.println("    </param>");
	    toFile.println("    ");
	    toFile.println("    <param>");
	    toFile.println("        <name>ccdNonLinearityFilename</name>");
	    toFile.println("        <value>"+request.getParameter("ccdNonLinearityFilename")+"</value>");
	    toFile.println("        <help>Filename for the REF_APP_CCDLinearisation fits file containing the CCD non-linearity spline coefficients</help>");
	    toFile.println("    </param>");
	    toFile.println("    ");
	    toFile.println("    <param>");
	    toFile.println("        <name>biasMean</name>");
	    toFile.println("        <value>"+request.getParameter("biasMean")+"</value>");
	    toFile.println("	    <type>double</type>");
	    toFile.println("        <help>Mean of Gaussian used to simulate the bias frame</help>");
	    toFile.println("    </param>");
	    toFile.println("    ");
	    toFile.println("    <param>");
	    toFile.println("        <name>biasWidth</name>");
	    toFile.println("        <value>"+request.getParameter("biasWidth")+"</value>");
	    toFile.println("	    <type>double</type>");
	    toFile.println("        <help>Width of Gaussian used to simulate the bias frame</help>");
	    toFile.println("    </param>");
	    toFile.println("    ");
	    toFile.println("    <param>");
	    toFile.println("        <name>biasNoiseSeed</name>");
	    toFile.println("        <value>"+request.getParameter("biasNoiseSeed")+"</value>");
	    toFile.println("	    <type>int</type>");
	    toFile.println("        <help>Random number generator seed for Gaussian bias noise</help>");
	    toFile.println("    </param>");
	    toFile.println("    ");
	    toFile.println("</module>");

	}
	    
	if (request.getParameter("runImageWriter")!=null) {

	    toFile.println("");
	    toFile.println("<module>");
	    toFile.println("  <name>ImageWriter</name>");
	    toFile.println("");
	    toFile.println("    <param>");
	    toFile.println("        <name>writeUnstackedImages</name>");
	    toFile.println("        <value>"+(request.getParameter("writeUnstackedImages")!=null ? true : false)+"</value>");
	    toFile.println("	    <type>bool</type>");
	    toFile.println("        <help>Set to true to output fits files containing unstacked images</help>");
	    toFile.println("    </param>");
	    toFile.println("    ");
	    toFile.println("    <param>");
	    toFile.println("        <name>writeStackedImages</name>");
	    toFile.println("        <value>"+(request.getParameter("writeStackedImages")!=null ? true : false)+"</value>");
	    toFile.println("	    <type>bool</type>");
	    toFile.println("        <help>Set to true to output fits files containing stacked images</help>");
	    toFile.println("    </param>");
	    toFile.println("    ");
	    toFile.println("    <param>");
	    toFile.println("        <name>doublePrecisionStackedImages</name>");
	    toFile.println("        <value>"+request.getParameter("doublePrecisionStackedImages")+"</value>");
	    toFile.println("	    <type>bool</type>");
	    toFile.println("        <help>Set to true to output stacked image pixel values as double precision rather than uint32</help>");
	    toFile.println("    </param>");
	    toFile.println("    ");
	    toFile.println("    <param>");
	    toFile.println("        <name>stackingMethod</name>");
	    toFile.println("        <value>"+request.getParameter("stackingMethod")+"</value>");
	    toFile.println("        <help>Stacking method: coadd (coaddition) or mean (mean value for each pixel)</help>");
	    toFile.println("    </param>");
	    toFile.println("    ");
	    toFile.println("    <param>");
	    toFile.println("        <name>marginStackingMethod</name>");
	    toFile.println("        <value>"+request.getParameter("marginStackingMethod")+"</value>");
	    toFile.println("        <help>Stacking method for CCD margins: coadd (coaddition) or mean (mean value for each pixel)</help>");
	    toFile.println("    </param>");
	    toFile.println("    ");
	    toFile.println("    <param>");
	    toFile.println("        <name>marginMode</name>");
	    toFile.println("        <value>"+request.getParameter("marginMode")+"</value>");
	    toFile.println("        <help>Processing mode for CCD margins: image, reduced or total_collapsed</help>");
	    toFile.println("    </param>");
	    toFile.println("    ");
	    toFile.println("    <param>");
	    toFile.println("        <name>writeImagettes</name>");
	    toFile.println("        <value>"+(request.getParameter("writeImagettes")!=null ? true : false)+"</value>");
	    toFile.println("	    <type>bool</type>");
	    toFile.println("        <help>Set to true to output a fits file containing small images for each exposure centred on the PSF barycentre</help>");
	    toFile.println("    </param>");
	    toFile.println("    ");
	    toFile.println("    <param>");
	    toFile.println("        <name>dynamicImagettes</name>");
	    toFile.println("        <value>"+(request.getParameter("croppingMethod").equals("dynamic") ? true : false)+"</value>");
	    toFile.println("	    <type>bool</type>");
	    toFile.println("        <help>Flag to indicate if imagette extraction should be dynamic (the centre of the imagette follows the centroid) rather than static (fixed position on CCD - forced if the imagettes are stacked)</help>");
	    toFile.println("    </param>");
	    toFile.println("    ");
	    toFile.println("    <param>");
	    toFile.println("        <name>stackImagettes</name>");
	    toFile.println("        <value>"+(request.getParameter("stackImagettes")!=null ? true : false)+"</value>");
	    toFile.println("	    <type>bool</type>");
	    toFile.println("        <help>Flag to indicate whether or not to stack the imagettes (dynamicImagettes imagettes will disabled if the imagette stacking number is >1)</help>");
	    toFile.println("    </param>");
	    toFile.println("    ");
	    toFile.println("    <param>");
	    toFile.println("        <name>imagetteShape</name>");
	    toFile.println("        <value>"+request.getParameter("imagetteShape")+"</value>");
	    toFile.println("        <help>Shape of the imagettes: either circular or rectangular</help>");
	    toFile.println("    </param>");
	    toFile.println("    ");
	    toFile.println("    <param>");
	    toFile.println("        <name>subarrayShape</name>");
	    toFile.println("        <value>"+request.getParameter("subarrayShape")+"</value>");
	    toFile.println("        <help>Shape of the sub-array images: either circular or rectangular</help>");
	    toFile.println("    </param>");
	    toFile.println("    ");
	    toFile.println("    <param>");
	    toFile.println("        <name>writeCentroid</name>");
	    toFile.println("        <value>"+(request.getParameter("writeCentroid")!=null ? true : false)+"</value>");
	    toFile.println("	    <type>bool</type>");
	    toFile.println("        <help>Set to true to output a fits file containing the onboard calculated centroid (mean PSF truth position) for each exposure</help>");
	    toFile.println("    </param>");
	    toFile.println("    ");
	    toFile.println("    <param>");
	    toFile.println("        <name>writeTruthData</name>");
	    toFile.println("        <value>"+(request.getParameter("writeTruthData")!=null ? true : false)+"</value>");
	    toFile.println("	    <type>bool</type>");
	    toFile.println("        <help>Set to true to output a fits file containing truth information</help>");
	    toFile.println("    </param>");
	    toFile.println("    ");
	    toFile.println("    <param>");
	    toFile.println("        <name>imagetteSize</name>");
	    toFile.println("        <value>"+request.getParameter("imagetteSize")+"</value>");
	    toFile.println("	    <type>int</type>");
	    toFile.println("        <help>Size of imagette in number of pixels along each side of a square</help>");
	    toFile.println("    </param>");
	    toFile.println("    ");
	    toFile.println("    <param>");
	    toFile.println("        <name>omitEarthOccultation</name>");
	    toFile.println("        <value>"+(request.getParameter("omitEarthOccultation")!=null ? true : false)+"</value>");
	    toFile.println("	    <type>bool</type>");
	    toFile.println("        <help>Set to true to only output images for which the target is not occulted by the Earth</help>");
	    toFile.println("    </param>");
	    toFile.println("    ");
	    toFile.println("    <param>");
	    toFile.println("        <name>omitSAA</name>");
	    toFile.println("        <value>"+(request.getParameter("omitSAA")!=null ? true : false)+"</value>");
	    toFile.println("	    <type>bool</type>");
	    toFile.println("        <help>Set to true to only output images for which the spacecraft is not within the SAA</help>");
	    toFile.println("    </param>");
	    toFile.println("    ");
	    toFile.println("    <param>");
	    toFile.println("        <name>omitStrayLight</name>");
	    toFile.println("        <value>"+(request.getParameter("omitStrayLight")!=null ? true : false)+"</value>");
	    toFile.println("	    <type>bool</type>");
	    toFile.println("        <help>Set to true to only output images for which the stray light is below threshold</help>");
	    toFile.println("    </param>");
	    toFile.println("    ");
	    toFile.println("    <param>");
	    toFile.println("        <name>numberOfCEsToOmitAtStart</name>");
	    toFile.println("        <value>"+request.getParameter("numberOfCEsToOmitAtStart")+"</value>");
	    toFile.println("	    <type>int</type>");
	    toFile.println("        <help>Number of stacked images to be discarded at the start of the simulation</help>");
	    toFile.println("    </param>");
	    toFile.println("    ");
	    toFile.println("    <param>");
	    toFile.println("        <name>onlyWriteEveryNthCE</name>");
	    toFile.println("        <value>"+request.getParameter("onlyWriteEveryNthCE")+"</value>");
	    toFile.println("	    <type>int</type>");
	    toFile.println("        <help>If the value is N, only every Nth stacked image will be written out. All images are written out if the value is 0 or 1.</help>");
	    toFile.println("    </param>");
	    toFile.println("    ");
	    toFile.println("    <param>");
	    toFile.println("        <name>truthBarycentre</name>");
	    toFile.println("        <value>false</value>");
	    toFile.println("	    <type>bool</type>");
	    toFile.println("        <help>Set to true to use the truth barycentre rather than calculating the barycentre through photometry on the final image</help>");
	    toFile.println("    </param>");
	    toFile.println("    ");
	    toFile.println("    <param>");
	    toFile.println("        <name>targetLocationFromJitter</name>");
	    toFile.println("        <value>"+request.getParameter("targetLocationFromJitter")+"</value>");
	    toFile.println("	    <type>bool</type>");
	    toFile.println("        <help>Set to true to define the target location using the jitter offsets in order to have a different target location for each image</help>");
	    toFile.println("    </param>");
	    toFile.println("    ");
	    toFile.println("    <param>");
	    toFile.println("        <name>ceCounterOffset</name>");
	    toFile.println("        <value>"+request.getParameter("ceCounterOffset")+"</value>");
	    toFile.println("	    <type>int</type>");
	    toFile.println("        <help>Initial offset to the CE counter</help>");
	    toFile.println("    </param>");
	    toFile.println("    ");
	    toFile.println("</module>");

	}
	
	if (request.getParameter("runHKWriter")!=null) {

	    toFile.println("");
	    toFile.println("<module>");
	    toFile.println("  <name>HKWriter</name>");
	    toFile.println("");
	    toFile.println("    <param>");
	    toFile.println("        <name>sdsCounterOffset</name>");
	    toFile.println("        <value>"+request.getParameter("sdsCounterOffset")+"</value>");
	    toFile.println("	    <type>int</type>");
	    toFile.println("        <help>Initial offset to the SDS counter</help>");
	    toFile.println("    </param>");
	    toFile.println("    ");
	    toFile.println("</module>");

	}
	    
	//if (request.getParameter("runDataReduction")!=null) {

	    toFile.println("");
	    toFile.println("<module>");
	    toFile.println("  <name>DataReduction</name>");
	    toFile.println("");
	    toFile.println("    <param>");
	    toFile.println("        <name>writeIncidentLightCurve</name>");
	    toFile.println("        <value>true</value>");
	    toFile.println("	    <type>bool</type>");
	    toFile.println("        <help>Set to true to output a fits file containing the intrinsic flux time series of the target star</help>");
	    toFile.println("    </param>");
	    toFile.println("    ");
	    toFile.println("    <param>");
	    toFile.println("        <name>imageDirectory</name>");
	    toFile.println("        <value>null</value>");
	    toFile.println("        <help>Full path of directory containing stacked images (optional)</help>");
	    toFile.println("    </param>");
	    toFile.println("    ");
	    toFile.println("    <param>");
	    toFile.println("        <name>extractLightCurve</name>");
	    toFile.println("        <value>"+(request.getParameter("extractLightCurve")!=null ? true : false)+"</value>");
	    toFile.println("	    <type>bool</type>");
	    toFile.println("        <help>Set to true to output a fits file containing the extracted light curve</help>");
	    toFile.println("    </param>");
	    toFile.println("    ");
	    toFile.println("    <param>");
	    toFile.println("        <name>generateNoiseCurve</name>");
	    toFile.println("        <value>"+(request.getParameter("generateNoiseCurve")!=null ? true : false)+"</value>");
	    toFile.println("	    <type>bool</type>");
	    toFile.println("        <help>Set to true to output a fits table containing the standard deviation of the light curve as a function of the binning in time</help>");
	    toFile.println("    </param>");
	    toFile.println("    ");
	    toFile.println("    <param>");
	    toFile.println("        <name>truthBarycentre</name>");
	    toFile.println("        <value>true</value>");
	    toFile.println("	    <type>bool</type>");
	    toFile.println("        <help>Set to true to use the truth barycentre rather than calculating the barycentre through photometry on the final image</help>");
	    toFile.println("    </param>");
	    toFile.println("    ");
	    toFile.println("    <param>");
	    toFile.println("        <name>subtractFlatField</name>");
	    toFile.println("        <value>"+(request.getParameter("subtractFlatField")!=null ? true : false)+"</value>");
	    toFile.println("	    <type>bool</type>");
	    toFile.println("        <help>Set to true to perform flat field correction during light curve extraction</help>");
	    toFile.println("    </param>");
	    toFile.println("    ");
	    toFile.println("</module>");

	//}
	    
	if (request.getParameter("runExampleModule")!=null) {
		
	    toFile.println("");
	    toFile.println("<module>");
	    toFile.println("  <name>ExampleModule</name>");
	    toFile.println("");
	    toFile.println("  	<param>");
	    toFile.println("        <name>parameter1</name>");
	    toFile.println("        <value>"+request.getParameter("parameter1")+"</value>");
	    toFile.println("	    <type>double</type>");
	    toFile.println("        <help>name of first parameter</help>");
	    toFile.println("    </param>");
	    toFile.println("    ");
	    toFile.println("  	<param>");
	    toFile.println("        <name>parameter2</name>");
	    toFile.println("        <value>"+request.getParameter("parameter2")+"</value>");
	    toFile.println("	    <type>int</type>");
	    toFile.println("        <help>name of second parameter</help>");
	    toFile.println("    </param>");
	    toFile.println("    ");
	    toFile.println("</module>");
	}

	toFile.println("");
	toFile.println("</program_params>");
    }
}
