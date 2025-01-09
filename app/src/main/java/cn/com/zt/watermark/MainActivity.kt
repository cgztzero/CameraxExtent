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
            checkPermission()
        }
    }

    private fun intentTo() {
        startActivity(Intent(this, ShootingActivity::class.java))
    }

    private fun checkPermission() {
        PermissionX.init(this)
            .permissions(
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO,
            )
            .request { allGranted, _, deniedList ->
                if (allGranted) {
                    //所有权限已经授权
                    Toast.makeText(this, "授权成功", Toast.LENGTH_SHORT).show()
                    intentTo()
                } else {
                    Toast.makeText(this, "拒绝权限: $deniedList", Toast.LENGTH_LONG).show()
                }
            }
    }
}


