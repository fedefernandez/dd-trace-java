package com.datadog.profiling.controller.openjdk;

import com.datadog.profiling.controller.openjdk.events.SmapEntryEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.util.Scanner;

public class SmapsUtil {
  public static void parseSmap(String filePath) throws ParseException {
    long startAddress = 0;
    long endAddress = 0;
    String perms = null;
    long offset = 0;
    String dev = null;
    int inode = 0;
    String pathname = null;

    long size = 0;
    long kernelPageSize = 0;
    long mmuPageSize = 0;
    long rss = 0;
    long pss = 0;
    long sharedClean = 0;
    long sharedDirty = 0;
    long privateClean = 0;
    long privateDirty = 0;
    long referenced = 0;
    long anonymous = 0;
    long lazyFree = 0;
    long anonHugePages = 0;
    long shmemPmdMapped = 0;
    long filePmdMapped = 0;
    long sharedHugetlb = 0;
    long privateHugetlb = 0;
    long swap = 0;
    long swapPss = 0;
    long locked = 0;

    boolean thpEligible = false;
    String[] vmFlags = null;
    try (Scanner scanner = new Scanner(new File(filePath))) {
      while (scanner.hasNextLine()) {
        var addresses = scanner.next().split("-");
        startAddress = Long.parseLong(addresses[0], 16);
        endAddress = Long.parseLong(addresses[1], 16);
        perms = scanner.next();
        offset = scanner.nextLong(16);
        dev = scanner.next();

        // todo remove?
        scanner.reset();
        inode = scanner.nextInt();
        if (scanner.hasNext()) {
          pathname = scanner.next();
        }

        for (int i = 0; i < 23; i++) {
          switch (scanner.next()) {
            case "Size:":
              size = scanner.nextLong();
              scanner.next();
              break;
            case "KernelPageSize:":
              kernelPageSize = scanner.nextLong();
              scanner.next();
              break;
            case "MMUPageSize:":
              mmuPageSize = scanner.nextLong();
              scanner.next();
              break;
            case "Rss":
              rss = scanner.nextLong();
              scanner.next();
              break;
            case "Pss:":
              pss = scanner.nextLong();
              scanner.next();
              break;
            case "Shared_Clean:":
              sharedClean = scanner.nextLong();
              scanner.next();
              break;
            case "Shared_Dirty:":
              sharedDirty = scanner.nextLong();
              scanner.next();
              break;
            case "Private_Clean:":
              privateClean = scanner.nextLong();
              scanner.next();
              break;
            case "Private_Dirty:":
              privateDirty = scanner.nextLong();
              scanner.next();
              break;
            case "Referenced:":
              referenced = scanner.nextLong();
              scanner.next();
              break;
            case "Anonymous:":
              anonymous = scanner.nextLong();
              scanner.next();
              break;
            case "LazyFree":
              lazyFree = scanner.nextLong();
              scanner.next();
              break;
            case "AnonHugePages":
              anonHugePages = scanner.nextLong();
              scanner.next();
              break;
            case "ShmemPmdMapped:":
              shmemPmdMapped = scanner.nextLong();
              scanner.next();
              break;
            case "FilePmdMapped:":
              filePmdMapped = scanner.nextLong();
              scanner.next();
              break;
            case "Shared_Hugetlb:":
              sharedHugetlb = scanner.nextLong();
              scanner.next();
              break;
            case "Private_Hugetlb:":
              privateHugetlb = scanner.nextLong();
              scanner.next();
              break;
            case "Swap:":
              swap = scanner.nextLong();
              scanner.next();
              break;
            case "SwapPss:":
              swapPss = scanner.nextLong();
              scanner.next();
              break;
            case "Locked:":
              locked = scanner.nextLong();
              scanner.next();
              break;
            case "THPeligible:":
              thpEligible = scanner.nextInt() == 1;
              break;
            case "VmFlags:":
              scanner.skip("\\s+");
              vmFlags = scanner.nextLine().split(" ");
              break;
            default:
              break;
          }
        }
        new SmapEntryEvent(
                startAddress,
                endAddress,
                perms,
                offset,
                dev,
                inode,
                pathname,
                size,
                kernelPageSize,
                mmuPageSize,
                rss,
                pss,
                sharedClean,
                sharedDirty,
                privateClean,
                privateDirty,
                referenced,
                anonymous,
                lazyFree,
                anonHugePages,
                shmemPmdMapped,
                filePmdMapped,
                sharedHugetlb,
                privateHugetlb,
                swap,
                swapPss,
                locked,
                thpEligible,
                vmFlags)
            .commit();
      }
    } catch (FileNotFoundException e) {
      System.out.println("File not found: " + filePath);
    }
  }
}
