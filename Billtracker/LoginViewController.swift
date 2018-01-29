//
//  LoginViewController.swift
//  Billtracker
//
//  Created by Valdet D. on 29.01.18.
//  Copyright © 2018 Valdet D.. All rights reserved.
//

import UIKit
import FirebaseAuth

class LoginViewController: UIViewController, UITextFieldDelegate {

    @IBOutlet weak var emailTextfield: UITextField!
    @IBOutlet weak var passwordTextfield: UITextField!
    
    @IBOutlet weak var confirmButton: UIButton!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        emailTextfield.delegate = self
        passwordTextfield.delegate = self 
        // Do any additional setup after loading the view.
    }
    
    func textFieldShouldReturn(_ textField: UITextField) -> Bool {
        textField.resignFirstResponder()
        return true
    }
    
    
    @IBAction func buttonLoginHandler(_ sender: UIButton) {
        
        guard let email = emailTextfield.text, !email.isEmpty else {
             let alert = UIAlertController(title: "No Email", message: "Please enter email address", preferredStyle: .alert)
                    alert.addAction(UIAlertAction(title: NSLocalizedString("OK", comment: "Default action"), style: .`default`, handler: { _ in
                        NSLog("The \"OK\" alert occured.")
                    }))
            
            return
        }
        
        guard let password = emailTextfield.text, !password.isEmpty else {
            let alert = UIAlertController(title: "No Password", message: "Please enter your password", preferredStyle: .alert)
            alert.addAction(UIAlertAction(title: NSLocalizedString("OK", comment: "Default action"), style: .`default`, handler: { _ in
                NSLog("The \"OK\" alert occured.")
            }))
            
            return
        }
        
        Auth.auth().signIn(withEmail: email, password: password, completion: { (user, error) in
            if let error = error {
                switch error {
                case AuthErrorCode.invalidEmail:
                    let alert = UIAlertController(title: "Wrong Email", message: "Please enter email address", preferredStyle: .alert)
                    alert.addAction(UIAlertAction(title: NSLocalizedString("OK", comment: "Default action"), style: .`default`, handler: { _ in
                        NSLog("The \"OK\" alert occured.")
                    }))
                case AuthErrorCode.wrongPassword:
                    let alert = UIAlertController(title: "Wrong Password", message: "Please enter your password", preferredStyle: .alert)
                    alert.addAction(UIAlertAction(title: NSLocalizedString("OK", comment: "Default action"), style: .`default`, handler: { _ in
                        NSLog("The \"OK\" alert occured.")
                    }))
                default:
                    break
                }
            } else {
                if let user = user {
                    UserDefaults.standard.set(user.uid, forKey: "userID")
                    self.performSegue(withIdentifier: "SingnInSegue", sender: nil)
                }
            }
        })
    }
}
