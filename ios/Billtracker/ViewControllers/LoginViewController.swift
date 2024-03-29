//
//  LoginViewController.swift
//  Billtracker
//
//  Created by Valdet Dodaj on 29.01.18.
//  Copyright © 2018 Valdet Dodaj. All rights reserved.
//

import UIKit
import FirebaseAuth

class LoginViewController: UIViewController, UITextFieldDelegate {

    @IBOutlet weak var emailTextfield: UITextField!
    @IBOutlet weak var passwordTextfield: UITextField!
    @IBOutlet weak var confirmButton: UIButton!
    
    var auth: Auth!
    
    override func viewDidLoad() {
        
        super.viewDidLoad()
        emailTextfield.delegate = self
        passwordTextfield.delegate = self
        
        emailTextfield.text = "billtracker2017@gmail.com"
        passwordTextfield.text = "hdmstuttgart!"
        auth = Auth.auth()
        // Do any additional setup after loading the view.
    }
    
    func textFieldShouldReturn(_ textField: UITextField) -> Bool {
        textField.resignFirstResponder()
        return true
    }
    
    // Global (View), damit die Tastatur verschwindet, wenn man neben dem Textfeld klickt etc.
    override func touchesBegan(_ touches: Set<UITouch>, with event: UIEvent?) {
        view.endEditing(true)
    }
    
    @IBAction func buttonLoginHandler(_ sender: UIButton) {
        print("buttonLoginHandler")
        print("mail", emailTextfield.text)
        print("pw", passwordTextfield.text)
        guard let email = emailTextfield.text, !email.isEmpty else {
             let alert = UIAlertController(title: "No Email", message: "Please enter email address", preferredStyle: .alert)
                    alert.addAction(UIAlertAction(title: NSLocalizedString("OK", comment: "Default action"), style: .`default`, handler: { _ in
                        NSLog("The \"OK\" alert occured.")
                    }))
            return
        }
        
        guard let password = passwordTextfield.text, !password.isEmpty else {
            let alert = UIAlertController(title: "No Password", message: "Please enter your password", preferredStyle: .alert)
            alert.addAction(UIAlertAction(title: NSLocalizedString("OK", comment: "Default action"), style: .`default`, handler: { _ in
                NSLog("The \"OK\" alert occured.")
            }))
            return
        }
        
        auth.signIn(withEmail: email, password: password, completion: { (user, error) in
            if let error = error {
                switch error {
                case AuthErrorCode.invalidEmail:
                    print("login invalidEmail")
                    let alert = UIAlertController(title: "Wrong Email", message: "Please enter email address", preferredStyle: .alert)
                    alert.addAction(UIAlertAction(title: NSLocalizedString("OK", comment: "Default action"), style: .`default`, handler: { _ in
                        NSLog("The \"OK\" alert occured.")
                    }))
                case AuthErrorCode.wrongPassword:
                    print("login wrongPassword")
                    let alert = UIAlertController(title: "Wrong Password", message: "Please enter your password", preferredStyle: .alert)
                    alert.addAction(UIAlertAction(title: NSLocalizedString("OK", comment: "Default action"), style: .`default`, handler: { _ in
                        NSLog("The \"OK\" alert occured.")
                    }))
                default:
                    break
                }
            } else {
                if let user = user {
                    print("login successful")
                    UserDefaults.standard.set(user.uid, forKey: "userID")
                    self.performSegue(withIdentifier: "SingnInSegue", sender: nil)
                }
            }
        })
    }
}
