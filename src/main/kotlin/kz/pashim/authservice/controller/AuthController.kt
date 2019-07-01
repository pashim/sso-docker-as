package kz.pashim.authservice.controller

import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Controller


@Controller
class FooController {

    @PreAuthorize("#oauth2.hasScope('read')")
    @RequestMapping(method = [RequestMethod.GET], value = ["/operation/{id}"])
    @ResponseBody
    fun findById(@PathVariable id: Long): Test {
        return Test(id, "pashim")
    }
}

data class Test (
    val id: Long = 0,
    val name: String? = null
)