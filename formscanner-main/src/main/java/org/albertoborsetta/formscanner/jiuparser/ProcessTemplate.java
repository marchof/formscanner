/*
 * ProcessTemplate.java
 *
 * Created on June 30, 2007, 12:12 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.albertoborsetta.formscanner.jiuparser;

import net.sourceforge.jiu.data.Gray8Image;

/**
 *
 * @author Aaditeshwar Seth
 */
public class ProcessTemplate {	

    public static void main(String args[]) {
        String filename = args[0];

        Gray8Image grayimage = ImageUtil.readImage(filename);
        
        
        ImageManipulation imageMan = new ImageManipulation(grayimage);
        imageMan.locateConcentricCircles();
        // imageMan.locateMarks();
        
        // imageMan.writeAscTemplate(filename + ".asc");
        // imageMan.writeConfig(filename + ".config");
    }    
}
