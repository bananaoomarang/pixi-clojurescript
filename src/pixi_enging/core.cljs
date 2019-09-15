(ns ^:figwheel-hooks pixi-engine.core
  (:require
   [oops.core :refer [oget+]]
   [goog.dom :as gdom]
   [pixi]))

(defonce PIXIApplication (.-Application pixi))
(defonce Sprite (.-Sprite pixi))
(defonce pixi-app (PIXIApplication.))
(defonce loader (.-loader pixi-app))
(defonce stage (.-stage pixi-app))
(defonce chickadee nil)
(defonce key-state {})

(def SPEED 5)

(defn get-app-element []
  (gdom/getElement "app"))

(gdom/appendChild (get-app-element) (.-view pixi-app))

(defn get-resource [name]
  (let [resources (.. pixi-app -loader -resources)]
    (oget+ resources name)))

(defn create-sprite [resource]
  (let [sprite (Sprite. (.-texture (get-resource resource)))]
    (set! (.-vx sprite) 0)
    (set! (.-vy sprite) 0)
    sprite))

(defn add-child! [stage sprite]
  (. stage addChild sprite))

(defn set-vel-x! [sprite v]
  (set! (.-vx sprite) v))

(defn set-vel-y! [sprite v]
  (set! (.-vy sprite) v))

(defn update-sprite! [dt sprite]
  (let [x (.-x sprite)
        y (.-y sprite)
        vx (.-vx sprite)
        vy (.-vy sprite)]
    (set! (.-x sprite) (+ x (* dt vx)))
    (set! (.-y sprite) (+ y (* dt vy)))))

(defn on-keydown [e]
  (let [key (.-key e)]
    (set! key-state (assoc key-state (keyword key) true))))

(defn on-keyup [e]
  (let [key (.-key e)]
    (set! key-state (assoc key-state (keyword key) false))))

(defn key-subscribe! []
  (. js/window addEventListener "keydown" on-keydown false)
  (. js/window addEventListener "keyup" on-keyup false))

(defn update-bird! [dt]
  "Move the bird if they click the arrows"

  (let [up (:ArrowUp key-state)
        down (:ArrowDown key-state)
        left (:ArrowLeft key-state)
        right (:ArrowRight key-state)]

    (if left (set-vel-x! chickadee (* -1 SPEED)))
    (if right (set-vel-x! chickadee SPEED))
    (if up (set-vel-y! chickadee (* -1 SPEED)))
    (if down (set-vel-y! chickadee SPEED))

    (if (or (and left right) (and (not left) (not right)))
      (set-vel-x! chickadee 0))

    (if (or (and up down) (and (not up) (not down)))
      (set-vel-y! chickadee 0)))

  (update-sprite! dt chickadee))

(defn update-game! [dt]
  (update-bird! dt))

(defn setup []
  (set! chickadee (create-sprite "chickadee"))
  (add-child! stage chickadee)

  (. (.-ticker pixi-app) add update-game!)
  (key-subscribe!))

(defn init! []
  "Init stuff"

  (-> loader
      (. add "chickadee" "chickadee.png")
      (. load setup)))

(init!)

;; specify reload hook with ^;after-load metadata
(defn ^:after-load on-reload []
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
)
