package com.aeiou.xin.gitrepo.controller;

import com.aeiou.xin.gitrepo.service.GitService;
import com.aeiou.xin.gitrepo.utils.ByteUtils;
import com.aeiou.xin.gitrepo.utils.CommandUtils;
import com.aeiou.xin.gitrepo.utils.FileUtils;
import com.aeiou.xin.gitrepo.utils.GzipUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.nio.charset.Charset;

/**
 * @author asuis
 * @version: GitController.java 1/25/19:8:18 PM
 */
@RestController
@RequestMapping("{repo}")
public class GitController {

    private final static String REPO_PATH = "/media/asuis/kit/workspace/git/";

    @GetMapping(value = "/info/refs", produces = "text/plain")
    public byte[] refsu(@RequestParam("service") String service, HttpServletResponse response, @PathVariable String repo) {

        if (!repo.endsWith(".git")) {
            throw new RuntimeException();
        }
        CommandUtils.run(FileUtils.path(REPO_PATH+repo), "update-server-info");
        return FileUtils.getFileContent(REPO_PATH+repo+"/info/refs");
    }
    @GetMapping(value = "/info/refs", params = "service=service=git-upload-pack", produces = "application/x-git-upload-pack-advertisement")
    public byte[] refsup(@RequestParam("service") String service, @PathVariable String repo) {
        if (!repo.endsWith(".git")) {
            throw new RuntimeException();
        }
        String path = REPO_PATH + repo;
        if (service.startsWith("git-")) {
            service = service.substring(4);
        }
        byte[] refs = GitService.getRefs(path, service);
        byte[] data = GitService.packetWrite("# service=git-"+ service +"\n");
        return ByteUtils.merge(data, "0000".getBytes(Charset.forName("utf-8")), refs);
    }
    @GetMapping(value = "/info/refs", params = "service=git-receive-pack", produces = "application/x-git-receive-pack-advertisement")
    public byte[] refs(@RequestParam("service") String service, @PathVariable String repo) {
        if (!repo.endsWith(".git")) {
            throw new RuntimeException();
        }
        String path = REPO_PATH + repo;
        if (service.startsWith("git-")) {
            service = service.substring(4);
        }
        byte[] refs = GitService.getRefs(path, service);
        byte[] data = GitService.packetWrite("# service=git-"+ service +"\n");
        byte[] bytes = new byte[] {0,0,0,0};
        return ByteUtils.merge(data, bytes, refs);
    }
    @PostMapping("git-upload-pack")
    public byte[] gitUploadPack(@RequestBody byte[] data, @RequestHeader("Content-type") String contentType, @RequestHeader("Content-Encoding") String contentEncoding, HttpServletResponse response, @PathVariable String repo) {
        if (!repo.endsWith(".git")) {
            throw new RuntimeException();
        }
        final String SERVICE = "upload-pack";

        if (!contentType.equals(String.format("application/x-git-%s-request", SERVICE))) {
            throw new ResourceNotAuthorizedException();
        }
        response.setContentType(String.format("application/x-git-%s-result", SERVICE));
        if ("gzip".equals(contentEncoding)) {
            data = GzipUtils.decompress(data);
        }
        return GitService.serviceCommand(REPO_PATH + repo, SERVICE, data);
    }
    @PostMapping("git-receive-pack")
    public byte[] gitReceivePack(@RequestBody byte[] data, @RequestHeader("Content-type") String contentType, @RequestHeader("Content-Encoding") String contentEncoding, HttpServletResponse response, @PathVariable String repo){
        if (!repo.endsWith(".git")) {
            throw new RuntimeException();
        }
        if (!"application/x-git-receive-pack-request".equals(contentType)) {
            throw new ResourceNotAuthorizedException();
        }
        response.setHeader("Content-type", "application/x-git-receive-pack-result");
        if ("gzip".equals(contentEncoding)) {
            data = GzipUtils.decompress(data);
        }
        return GitService.serviceCommand(REPO_PATH + repo,"receive-pack", data);
    }
    @GetMapping("HEAD")
    public String head(@PathVariable String repo) {
        if (!repo.endsWith(".git")) {
            throw new RuntimeException();
        }
        return GitService.sendHead(REPO_PATH + repo);
    }
    @GetMapping("/objects/info/packs")
    public byte[] objectInfoPacks(@PathVariable String repo, HttpServletResponse response) {
        if (!repo.endsWith(".git")) {
            throw new RuntimeException();
        }
        File path = FileUtils.path(REPO_PATH + repo + "objects/info/packs");
        if (!path.exists()) {
            CommandUtils.run(path, "update-server-info");
        }
        byte[] content = FileUtils.getFileContent(REPO_PATH + repo + "objects/info/packs");
        response.setContentType("text/plain; charset=utf-8");
        return content;
    }
    @GetMapping("/objects/{index}/{sha}")
    public byte[] objectHashSha(@PathVariable String repo,@PathVariable String index, @PathVariable String sha) {
        if (!repo.endsWith(".git")) {
            throw new RuntimeException();
        }
        return FileUtils.getFileContent(REPO_PATH + repo + "/" + index + "/" + sha);
    }
    @GetMapping("objects/pack/pack-{pack}.pack")
    public byte[] objectsPackDetails(@PathVariable String repo, @PathVariable String pack, HttpServletResponse response) {
        if (!repo.endsWith(".git")) {
            throw new RuntimeException();
        }
        String path = REPO_PATH + repo + "/" + String.format("pack/pack-%s.pack", pack);
        response.setContentType("text/plain; charset=utf-8");
        return FileUtils.getFileContent(path);
    }
    @GetMapping("/objects/pack/pack-{index}.idx")
    public byte[] objectsPackIndex (@PathVariable String repo, @PathVariable String index, HttpServletResponse response) {
        if (!repo.endsWith(".git")) {
            throw new RuntimeException();
        }
        String path = REPO_PATH + repo + "/" + String.format("pack/pack-%s.idx", index);
        response.setContentType("text/plain; charset=utf-8");
        return FileUtils.getFileContent(path);
    }
}
@ResponseStatus(value = HttpStatus.UNAUTHORIZED)
class ResourceNotAuthorizedException extends RuntimeException {}