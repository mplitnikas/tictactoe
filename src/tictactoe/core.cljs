(ns ^:figwheel-always tictactoe.core
    (:require [reagent.core :as reagent :refer [atom]]
              [cljs.test :refer-macros [deftest is]]))

(enable-console-print!)

(defn new-board [n]
  (vec (repeat n (vec (repeat n "B")))))

(def board-size 3)

(defonce app-state 
  (atom {:text "tic-tac-toe"
         :board (new-board board-size)
         :game-state :in-progress}))

(defn computer-move [board]
  (let [remaining-spots (for [i (range board-size)
                              j (range board-size)
                              :when (= (get-in board [j i]) "B")]
                          [j i])
        move (when (seq remaining-spots)
              (rand-nth remaining-spots))]
    (if move 
      (assoc-in board move "C")
      board)))

(deftest computer-move-test
  (is (= [["C"]]
        (computer-move [["B"]])))
  (is (= [["P"]]
        (computer-move [["P"]]))))

(defn straight [owner board [x y] [dx dy] n]
  (every? true?
    (for [i (range n)]
      (= (get-in board [(+ (* dx i) x)
                        (+ (* dy i) y)])
        owner))))

(defn win? [owner board n]
  (some true?
    (for [i (range board-size)
          j (range board-size)
          dir [[1 0] [0 1] [1 1] [1 -1]]]
      (straight owner board [i j] dir n))))

(deftest win?-test
  (is (win? "P" [["P"]] 1))
  (is (not (win? "P" [["P"]] 2)))
  (is (win? "P" [["C" "P"]
                 ["P" "C"]] 2)))

(defn full? [board]
  (every? #{"P" "C"} (apply concat board)))

(deftest full?-test
  (is (not (full? [["P" "B"]])))
  (is (full? [["P" "C"]])))

(defn game-status [board]
  (cond
    (win? "P" board board-size) :player-victory
    (win? "C" board board-size) :computer-victory
    (full? board) :draw
    :else :in-progress))

(defn game-status-message [status]
  (case status
    :player-victory "Winner: player!"
    :computer-victory "Winner: computer!"
    :draw "Winner: impersonal forces of entropy!"
    :in-progress "playing..."))

(defn elem-click [i j e]
  (if (= (:game-state @app-state) :in-progress)
    (do
      (swap! app-state assoc-in [:board j i] "P")
      (swap! app-state assoc-in [:game-state] (game-status (:board @app-state)))
      (if (= (:game-state @app-state) :in-progress)
        (do
          (swap! app-state update-in [:board] computer-move)
          (swap! app-state assoc-in [:game-state] (game-status (:board @app-state))))))))
      ; (prn (game-result-message (:game-state @app-state))))))

(defn blank [i j]
  [:rect {:width 0.9
          :height 0.9
          :fill "#a2abb5"
          :x i
          :y j
          :on-click (partial elem-click i j)}])

(defn circle [i j]
  [:circle
    {:r 0.4
     :cx (+ 0.45 i)
     :cy (+ 0.45 j)
     :stroke "#95d9da"
     :stroke-width 0.10
     :fill "none"}])

(defn cross [i j]
  [:g {:stroke "#ae8ca3"
       :stroke-width 0.35
       :stroke-linecap "round"
       :transform
       (str "translate(" (+ 0.5 i) "," (+ 0.5 j) ") "
            "scale(0.35)")}
    [:line {:x1 -1 :y1 -1 :x2 1 :y2 1}]
    [:line {:x1 1 :y1 -1 :x2 -1 :y2 1}]])  

(defn tictactoe []
  (prn @app-state)
  [:center
    [:h1 (:text @app-state)]
    [:div
      [:h2 (game-status-message (:game-state @app-state))]
      [:button
        {:on-click
          (fn new-game-click [e]
            (swap! app-state assoc-in [:board] (new-board board-size))
            (swap! app-state assoc-in [:game-state] :in-progress))}
        "New Game"]]
    [:svg
      {:view-box "0 0 3 3"
       :width 500
       :height 500}
      (doall (for [i (range (count (:board @app-state)))
                   j (doall (range (count (:board @app-state))))]
                (case (get-in @app-state [:board j i])
                    "B" ^{:key (gensym)} [blank i j]
                    "P" ^{:key (gensym)} [circle i j]
                    "C" ^{:key (gensym)} [cross i j]
                        [blank i j])))]])
    

(reagent/render-component [tictactoe]
                          (. js/document (getElementById "app")))

(defn on-js-reload []
  (prn "run tests")
  (prn (full?-test))
  (prn (win?-test))
  (prn (computer-move-test)))
