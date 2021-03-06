package org.opentripplanner.standalone;

import java.io.File;
import java.util.Map;

import javax.media.jai.TileCache;
import javax.swing.Renderer;

import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;

/**
 * This is replacing a Spring application context.
 */
public class OTPServer {

    private static final Logger LOG = LoggerFactory.getLogger(OTPServer.class);

    // will replace graphService
    private final Map<String, Router> routers = Maps.newHashMap();

    // Core OTP modules
    public GraphService graphService;
    public PathService pathService;
    public RoutingRequest routingRequest; // the prototype routing request which establishes default parameter values
    public PlanGenerator planGenerator;
    public SPTService sptService;

    // Optional Analyst Modules
    public Renderer renderer;
    public SPTCache sptCache;
    public TileCache tileCache;
    public IsoChroneSPTRenderer isoChroneSPTRenderer;
    public SampleGridRenderer sampleGridRenderer;
    public SurfaceCache surfaceCache;
    public PointSetCache pointSetCache;

    public TileRendererManager tileRendererManager;

    public Router getRouter(String routerId) {
        return routers.get(routerId);
    }

    public OTPServer (CommandLineParameters params, GraphService gs) {
        LOG.info("Wiring up and configuring server.");

        // Core OTP modules
        graphService = gs;
        routingRequest = new RoutingRequest();
        sptService = new GenericAStar();

        // Choose a PathService to wrap the SPTService, depending on expected maximum path lengths
        if (params.longDistance) {
            LongDistancePathService pathService = new LongDistancePathService(graphService, sptService);
            pathService.timeout = 10;
            this.pathService = pathService;
        } else {
            RetryingPathServiceImpl pathService = new RetryingPathServiceImpl(graphService, sptService);
            pathService.setFirstPathTimeout(10.0);
            pathService.setMultiPathTimeout(1.0);
            this.pathService = pathService;
            // cpf.bind(RemainingWeightHeuristicFactory.class,
            //        new DefaultRemainingWeightHeuristicFactoryImpl());
        }

        planGenerator = new PlanGenerator(graphService, pathService);
        tileRendererManager = new TileRendererManager(graphService);

        // Optional Analyst Modules.
        if (params.analyst) {
            tileCache = new TileCache(graphService);
            sptCache = new SPTCache(sptService, graphService);
            renderer = new Renderer(tileCache, sptCache);
            sampleGridRenderer = new SampleGridRenderer(graphService, sptService);
            isoChroneSPTRenderer = new IsoChroneSPTRendererAccSampling(graphService, sptService, sampleGridRenderer);
            surfaceCache = new SurfaceCache(30);
            pointSetCache = new DiskBackedPointSetCache(100, new File(params.pointSetDirectory));
        }

    }

    /**
     * Return an HK2 Binder that injects this specific OTPServer instance into Jersey web resources.
     * This should be registered in the ResourceConfig (Jersey) or Application (JAX-RS) as a singleton.
     * More on custom injection in Jersey 2:
     * http://jersey.576304.n2.nabble.com/Custom-providers-in-Jersey-2-tp7580699p7580715.html
     */
     public AbstractBinder makeBinder() {
        return new AbstractBinder() {
            @Override
            protected void configure() {
                bind(OTPServer.this).to(OTPServer.class);
            }
        };
    }

}
