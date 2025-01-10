package com.xperiencelabs.arapp

import android.media.MediaPlayer
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isGone
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.ar.core.Config
import io.github.sceneview.ar.ArSceneView
import io.github.sceneview.ar.node.ArModelNode
import io.github.sceneview.math.Position

class MainActivity : AppCompatActivity() {

    private lateinit var sceneView: ArSceneView
    lateinit var placeButton: ExtendedFloatingActionButton
    private lateinit var mediaPlayer: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inicialización del ArSceneView y configuración de la estimación de luz
        sceneView = findViewById<ArSceneView>(R.id.sceneView).apply {
            this.lightEstimationMode = Config.LightEstimationMode.DISABLED
        }

        // Inicialización del MediaPlayer con un archivo de audio
        mediaPlayer = MediaPlayer.create(this, R.raw.ad)

        // Configuración del botón de colocar modelo
        placeButton = findViewById(R.id.place)

        // Configuración del evento de clic en el botón para colocar modelos
        placeButton.setOnClickListener {
            placeModels()
        }
    }

    // Función para colocar tres modelos 3D diferentes en la escena
    private fun placeModels() {
        sceneView.planeRenderer.isVisible = false

        // Lista de modelos con sus respectivas ubicaciones y archivos GLB
        val models = listOf(
            Pair("models/mamut.glb", Position(0f, 0f, 0f)),          // Mamut en el centro
            Pair("models/dragon_car.glb", Position(-0.5f, 0f, -0.5f)), // Dragon Car a la izquierda
            Pair("models/angel_dragon.glb", Position(0.5f, 0f, -0.5f)) // Angel Dragon a la derecha
        )

        // Crear y añadir cada modelo a la escena
        models.forEach { (glbFileLocation, position) ->
            val modelNode = ArModelNode(sceneView.engine).apply {
                loadModelGlbAsync(
                    glbFileLocation = glbFileLocation,
                    scaleToUnits = 1f,
                    centerOrigin = position
                )
                onAnchorChanged = {
                    // Ocultar el botón cuando el modelo está anclado
                    placeButton.isGone = it != null
                }
            }
            // Agregar el nuevo nodo modelo a la escena
            sceneView.addChild(modelNode)
            modelNode.anchor()
        }
    }

    // Detener la reproducción de audio cuando la actividad está en pausa
    override fun onPause() {
        super.onPause()
        mediaPlayer.stop()
    }

    // Liberar recursos del MediaPlayer cuando la actividad se destruye
    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
    }
}
