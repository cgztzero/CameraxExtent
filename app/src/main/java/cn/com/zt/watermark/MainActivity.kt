package cn.com.zt.watermark

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import cn.com.zt.watermark.databinding.ActivityMainBinding
import com.permissionx.guolindev.PermissionX


class MainActivity : FragmentActivity() {
    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)

        binding.button.setOnClickListener {
            checkPermission(1)
        }

        binding.scanButton.setOnClickListener {
            checkPermission(2)
        }
    }

    private fun intentTo(type: Int) {
        if (type == 1) {
            startActivity(Intent(this, ShootingActivity::class.java))
        } else if (type == 2) {
            startActivity(Intent(this, ScanActivity::class.java))
        }

    }

    private fun checkPermission(type: Int) {
        PermissionX.init(this)
            .permissions(
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO,
            )
            .request { allGranted, _, deniedList ->
                if (allGranted) {
                    //所有权限已经授权
                    Toast.makeText(this, "grant success", Toast.LENGTH_SHORT).show()
                    intentTo(type)
                } else {
                    Toast.makeText(this, "refuse: $deniedList", Toast.LENGTH_LONG).show()
                }
            }
    }
}


