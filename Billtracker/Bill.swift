//
//  Bill.swift
//  Billtracker
//
//  Created by Kaan K. on 29.01.18.
//  Copyright © 2018 Valdet D.. All rights reserved.
//

import Foundation

class Bill {
    
    var id: String? {
        get {
            return self.id
        }
        set {
            self.id = newValue
        }
    }
    
    var imageId: String? {
        get {
            return self.imageId
        }
        set {
            self.imageId = newValue
        }
    }
    
    var category: String! {
        get {
            return self.category
        }
        set {
            self.category = newValue
        }
    }
    
    var date: UInt! {
        get {
            return self.date
        }
        set {
            self.date = newValue
        }
    }
    
    var sum: Double! {
        get {
            return self.sum
        }
        set {
            self.sum = newValue
        }
    }
    
    init(category: String, date: UInt, sum: Double) {
        self.category = category
        self.date = date
        self.sum = sum
    }

    
    func printDate() -> String {
        let d = Date(timeIntervalSinceReferenceDate: TimeInterval(date!))
        let formatter = DateFormatter()
        formatter.setLocalizedDateFormatFromTemplate("dd.MM.yyyy")
        return formatter.string(from: d)
    }


}
