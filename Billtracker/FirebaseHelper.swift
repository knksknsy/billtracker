//
//  FirebaseHelper.swift
//  Billtracker
//
//  Created by Valdet D. on 29.01.18.
//  Copyright © 2018 Valdet D.. All rights reserved.
//

import UIKit
import FirebaseDatabase
import FirebaseStorage


enum FirebaseStrings {
    static let postDirectory = "posts"
    static let imageDirectory = "images"
}

enum FirebaseHelper {
    
    private static let databaseReference = Database.database().reference()
    private static let storageReference = Storage.storage().reference()
    static let postsReference = databaseReference.child(FirebaseStrings.postDirectory)
    
//    static func saveDatabaseEntry(to directory: String, with recipe: Recipe, by user: String, completion: @escaping (Bool)->()) {
//        
//    }
    
    static func upload(image: UIImage, to directory: String, completion: @escaping (URL?)->()) {
        
    }
    
    static func downloadImage(for url: String, completion: @escaping (UIImage)->()) {
        
}
}
