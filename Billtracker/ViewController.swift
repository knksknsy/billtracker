//
//  ViewController.swift
//  Billtracker
//
//  Created by Valdet D. on 22.11.17.
//  Copyright © 2017 Valdet D.. All rights reserved.
//

import UIKit
import AVFoundation

class ViewController: UIViewController {

// die Button hier deklarieren
    
//    var backCamera: AVCaptureDevice?
//    var frontCamera: AVCaptureDevice?
//    var currentCamera: AVCaptureDevice?
//    var photoOutput: AVCapturePhotoOutput?
    
    
//    var cameraPreviewLayer: AVCaptureVideoPreviewLayer?
    //var videoPreviewLayer: AVCaptureVideoPreviewLayer?
    //captureSession = AVCaptureSession()
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        
        
//        setupCaptureSession()
//        setupDevice()
//        setupInputOutput()
//        setuptPreviewLayer()
//        startRunningCaptureSession()
        // Do any additional setup after loading the view, typically from a nib.
        //UIApplication.shared.statusBarView?.backgroundColor = UIColor.red
    
    }
    
    // Global (View), damit die Tastatur verschwindet, wenn man neben dem Textfeld klickt etc. 
    override func touchesBegan(_ touches: Set<UITouch>, with event: UIEvent?) {
        view.endEditing(true)
    }
    
//    func setupCaptureSession() {
//        captureSession.sessionPreset = AVCaptureSession.Preset.photo
//    }
//    
//    
//    func setupDevice() {
//        let deviceDicoverySession = AVCaptureDevice.DiscoverySession(deviceTypes: [AVCaptureDevice.DeviceType.builtInWideAngleCamera], mediaType: AVMediaType.video, position: AVCaptureDevice.Position.unspecified)
//        
//        let devices = deviceDicoverySession.devices
//        for device in devices {
//            if device.position == AVCaptureDevice.Position.back{
//                backCamera = device
//                
//            } else if device.position == AVCaptureDevice.Position.front {
//                frontCamera = device
//            
//            }
//        }
//        currentCamera = backCamera
//    }
//    
//    
//    func setupInputOutput() {
//        do {
//            let captureDeviceInput = try AVCaptureDeviceInput(device: currentCamera!)
//            captureSession.addInput(captureDeviceInput)
//            photoOutput = AVCapturePhotoOutput()
//            photoOutput?.setPreparedPhotoSettingsArray([AVCapturePhotoSettings(format: [AVVideoCodecKey: AVVideoCodecType.jpeg])], completionHandler: nil)
//            captureSession.addOutput(photoOutput!)
//        } catch  {
//            print(error)
//        }
//        
//    }
//    
//    func setuptPreviewLayer() {
//        cameraPreviewLayer = AVCaptureVideoPreviewLayer(session: captureSession)
//        cameraPreviewLayer?.videoGravity = AVLayerVideoGravity.resizeAspect
//        cameraPreviewLayer?.connection?.videoOrientation = AVCaptureVideoOrientation.portrait
//        cameraPreviewLayer?.frame = self.view.frame
//        self.view.layer.insertSublayer(cameraPreviewLayer!, at: 0)
//    }
//    
//    
//    func startRunningCaptureSession() {
//        captureSession.startRunning()
//        
//    }
//    
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    //override var prefersStatusBarHidden: Bool {
  //      return true
   // }

}

//extension UIApplication {
//    
//    var statusBarView: UIView? {
//        return value(forKey: "statusBar") as? UIView
//    }
//    
//}


