(ns ^:figwheel-hooks pixi-engine.core
  (:require
   [pixi-engine.wrapper :as game]
   [pixi-engine.pixi :as pixi]))

(defonce pixi-app (pixi/create-application))
(defonce stage (.-stage pixi-app))
(defonce chickadee nil)
(defonce key-state (atom {}))

(def SPEED 5)

(defn create-entity
  ([resource-name pos]
   (create-entity resource-name pos {}))
  ([resource-name pos {:keys [vel] :or {:vel (game/vec2 0 0)}}]
   (atom {:sprite (pixi/create-sprite (pixi/get-resource pixi-app resource-name))
          :x (game/vec-x pos)
          :y (game/vec-y pos)
          :vx (game/vec-x vel)
          :vy (game/vec-y vel)})))

(defn set-vel-x! [entity v]
  (swap! entity assoc :vx v))

(defn set-vel-y! [entity v]
  (swap! entity assoc :vy v))

(defn update-entity! [dt entity]
  (let [e @entity
        sprite (:sprite e)
        vx (:vx e)
        vy (:vy e)
        x (.-x sprite)
        y (.-y sprite)]
    (set! (.-x sprite) (+ x (* dt vx)))
    (set! (.-y sprite) (+ y (* dt vy)))))

(defn on-keydown [e]
  (let [key (.-key e)]
    (swap! key-state assoc (keyword key) true)))

(defn on-keyup [e]
  (let [key (.-key e)]
    (swap! key-state assoc (keyword key) false)))

(defn update-bird! [dt]
  "Move the bird if they click the arrows"

  (let [ks @key-state
        up (:ArrowUp ks)
        down (:ArrowDown ks)
        left (:ArrowLeft ks)
        right (:ArrowRight ks)]

    (if left (set-vel-x! chickadee (* -1 SPEED)))
    (if right (set-vel-x! chickadee SPEED))
    (if up (set-vel-y! chickadee (* -1 SPEED)))
    (if down (set-vel-y! chickadee SPEED))

    (if (or (and left right) (and (not left) (not right)))
      (set-vel-x! chickadee 0))

    (if (or (and up down) (and (not up) (not down)))
      (set-vel-y! chickadee 0)))

  (update-entity! dt chickadee))

(defn update-game! [dt]
  (update-bird! dt))

(def sprites {:chickadee "chickadee.png"})

(defn setup []
  (set! chickadee (create-entity "chickadee" (game/vec2 0 0)))
  (game/add-entity! stage chickadee))

(game/init!
 pixi-app
 {:sprites sprites
  :update update-game!
  :setup setup
  :on-keydown on-keydown
  :on-keyup on-keyup})

;; specify reload hook with ^;after-load metadata
(defn ^:after-load on-reload []
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
)
