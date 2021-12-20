(ns adventure.core
  (:require clojure.set
            [clojure.core.match :refer [match]]
            [clojure.string :as str])
  (:gen-class))

(def the-map
  {
   :Creppy-Tree {
      :seen false
      :has-found true
      :default-desc "There are a lot of very weirdly shaped trees here..."
      :first-desc "You realize you need your backpack!\n--Press 'i' to pickup some items from an area.\n--Press 'l' to look around the area."
      :second-desc "It looks like all the leaves have fallen from these creepy trees..."
      :title "Creepy Tree:\n"
      :dir {
            :north :Oil-Tankers
            :south :Cross-Walls
            :east :Red-Truck-and-Mobile-Home
            :west :Bathroom-Complex
            }
      :contents :note1
      :find "You seem to find a note in this drawer..."
      :first-message "--Press 'b' to see what you have in your bag."
      :second-message "There's nothing you need from this area right now..."
   }
   :Red-Truck-and-Mobile-Home {
      :seen false
      :has-found false
      :default-desc "It looks like you've stumbled upon a trailer park...\n"
      :first-desc ""
      :second-desc ""
      :title "Red Truck and Mobile Home\n"
      :dir {
            :north :Blue-Truck
            :south :Wood-Pillars
            :east :Creppy-Tree
            }
      :contents :note2
      :find "You seem to find a note in this drawer..."
      :first-message "--Press 'b' to see what you have in your bag."
      :second-message "There's nothing you need from this area right now..."
   }
   :Bathroom-Complex {
      :seen false
      :has-found false
      :default-desc "It looks like you've stumbled upon a bathroom complex?...\n"
      :first-desc ""
      :second-desc ""
      :title "Bathroom Complex\n"
      :dir {
            :north :Tunnel
            :south :Rock-Pile-Trio
            :west :Creppy-Tree
            }
      :contents :note3
      :find "You seem to find a note in this drawer..."
      :first-message "--Press 'b' to see what you have in your bag."
      :second-message "There's nothing you need from this area right now..."
            
   }
   :Oil-Tankers {
      :seen false
      :has-found false
      :default-desc "It looks like you've stumbled upon a range of oil tankers...\n"
      :first-desc ""
      :second-desc ""
      :title "Oil Tankers\n"
      :dir {
            :south :Creppy-Tree
            :east :Tunnel
            :west :Blue-Truck
            }
      :contents nil
      :find "You seem to find nothing in this drawer..."
      :first-message "--Press 'b' to see what you have in your bag."
      :second-message "There's nothing you need from this area right now..."
   }
   :Blue-Truck {
      :seen false
      :has-found false
      :default-desc "It looks like you've stumbled upon a row of blue trucks...\n"
      :first-desc ""
      :second-desc ""
      :title "Blue Truck\n"
      :dir {
            :south :Red-Truck-and-Mobile-Home
            :east :Oil-Tankers
            }
      :contents :note4
      :find "You seem to find a note in this drawer..."
      :first-message "--Press 'b' to see what you have in your bag."
      :second-message "There's nothing you need from this area right now..."
   }
   :Tunnel {
      :seen false
      :has-found false
      :default-desc "It looks like you've stumbled upon a deep dark tunnel...\n"
      :first-desc ""
      :second-desc ""
      :title "Tunnel\n"
      :dir {
            :south :Bathroom-Complex
            :west :Oil-Tankers
            }
      :contents :note5
      :find "You seem to find a note in this drawer..."
      :first-message "--Press 'b' to see what you have in your bag."
      :second-message "There's nothing you need from this area right now..."
   }
   :Cross-Walls {
      :seen false
      :has-found false
      :default-desc "It looks like you've stumbled upon a weird arrangement of cross walls...\n"
      :first-desc ""
      :second-desc ""
      :title "Cross Walls\n"
      :dir {
            :north :Creppy-Tree
            :south :Silo
            :east :Rock-Pile-Trio
            :west :Wood-Pillars
            }
      :contents :note6
      :find "You seem to find a note in this drawer..."
      :first-message "--Press 'b' to see what you have in your bag."
      :second-message "There's nothing you need from this area right now..."
   }
   :Wood-Pillars {
      :seen false
      :has-found false
      :default-desc "It looks like you've stumbled upon some wood pillars...\n"
      :first-desc ""
      :second-desc ""
      :title "Wood Pillars\n"
      :dir {
            :north :Red-Truck-and-Mobile-Home
            :east :Cross-Walls
            }
      :contents :note7
      :find "You seem to find a note in this drawer..."
      :first-message "--Press 'b' to see what you have in your bag."
      :second-message "There's nothing you need from this area right now..."
   }
   :Rock-Pile-Trio {
      :seen false
      :has-found false
      :default-desc "It looks like you've stumbled upon some rock piles...\n"
      :first-desc ""
      :second-desc ""
      :title "Rock Pile Trio\n"
      :dir {
            :north :Bathroom-Complex
            :west :Cross-Walls
            }
      :contents nil
      :find "You seem to find nothing in this drawer..."
      :first-message "--Press 'b' to see what you have in your bag."
      :second-message "There's nothing you need from this area right now..." 
   }
   :Silo {
      :seen false
      :has-found false
      :default-desc "It looks like you've stumbled upon a silo...\n"
      :first-desc ""
      :second-desc ""
      :title "Silo\n"
      :dir {:north :Cross-Walls}
      :contents :note8
      :find "You seem to find a note in this drawer..."
      :first-message "--Press 'b' to see what you have in your bag."
      :second-message "There's nothing you need from this area right now..."}})

(def adventurer
  {:location :Creppy-Tree
   :inventory #{}
   :tick 0
   :seen #{}})

(defn status [player]
  (let [location (player :location)]
    (println (str "\n" (-> the-map location :title)))

    (if (not (-> the-map location :seen))
      (if (contains? (get-in player [:inventory]) (-> the-map location :contents))
        (println (-> the-map location :second-desc))
        (println (-> the-map location :first-desc)))
      (println (-> the-map location :default-desc)))
    (def the-map (assoc-in the-map [location :seen] true))
    player))

(defn to-keywords [commands]
  (mapv keyword (str/split commands #"[.,?! ]+")))

(defn tock [player]
  (update-in player [:tick] inc)

(defn go [dir player]
   (let [location (player :location)
         dest (->> the-map location :dir dir)]
    (if (nil? dest)
      (do (println "\nYou can't go that way.")
        player)
      (do
        (tock (assoc-in player [:location] dest))))))

(defn item [player]
  (let [location (player :location)
        item (->> the-map location :contents)
        found (->> the-map location :has-found)]

    (if (or (= nil item) (not found))
      (do (println "\nThere isn't anything here.")
        player)
      (if (contains? (get-in player [:inventory]) item)
        (do (println (-> the-map location :second-message))
          player)
        (do (println (-> the-map location :first-message))
          (update-in player [:inventory] #(conj % item)))))))

(defn bag [player]
  (let [bag (get-in player [:inventory])]
    (if (empty? bag)
      (do (println "\nYour bag is empty.")
        player)
      (do (println "\nYou have:")
        (doseq [item bag]
          (println (str " " (str/replace (name item) #"-" " "))))
        player))))

(defn find-room [player]
  (let [location (player :location)
        clue (->> the-map location :find)]
    (if (= nil clue)
      (do (println "There aren't any clues here.")
        player)
      (do
        (println clue)
        (def the-map (assoc-in the-map [location :has-found] true))
        player))))

(defn look [player]
  (let [location (player :location)]
    (def the-map (assoc-in the-map [location :seen] false))
    player))

(defn quit [player]
  (println "\nYou did not escape the forest freely and you have died to the Slenderman :(\n")
  (System/exit 0))

(defn respond [player command]
      (match command
        [:l] (look player)
        [:f] (find-room player)
        [:w] (go :north player)
        [:s] (go :south player)
        [:d] (go :east player)
        [:a] (go :west player)
        [:r] (drop player)
        [:i] (item player)
        [:b] (bag player)
        [:q] (quit player)
        _  (do (println "\nThat is not a valid key.")
               player)))

(defn -main
  [& args]
  (println "Welcome to the Slenderman Game Clone! (Made by Daniel Rugutt using Clojure)\n")
  (println "You are in the middle of the Creppy Forrest during the middle of the night, and your objective is to collect all eight notes located in various areas of the forrest while avoiding the Slender Man.\n")
  (println "Be careful as the more notes you collect, the more difficult and dangerous it will be to complete your objective.\n\n")
  
  (println "Press 'w' to move north/up")
  (println "Press 'a' to move east/left")
  (println "Press 's' to move south/down")
  (println "Press 'd' to move west/right")
  
  (println "Press 'l' to look around the area")
  (println "Press 'f' to get a clue about the area")
  (println "Press i to pick up an item from an area")
  (println "Press b to check out your inventory in your backpack")
  (println "Press q to quit the game")
  
  
  (println "Good luck! :)\n\n")
  (loop [local-map the-map
         local-player adventurer]
    (let [pl (status local-player)
          _  (println "What do you want to do?\n")
          command (read-line)]
      (recur local-map (respond pl (to-keywords command))))))
  
  
